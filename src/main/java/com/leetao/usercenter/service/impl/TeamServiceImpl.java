package com.leetao.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leetao.usercenter.common.ErrorCode;
import com.leetao.usercenter.common.TeamStatus;
import com.leetao.usercenter.exception.BusinessException;
import com.leetao.usercenter.model.domain.Team;
import com.leetao.usercenter.model.domain.User;
import com.leetao.usercenter.model.domain.UserTeam;
import com.leetao.usercenter.model.dto.TeamQuery;
import com.leetao.usercenter.model.request.TeamJoinRequest;
import com.leetao.usercenter.model.request.TeamQuitRequest;
import com.leetao.usercenter.model.request.TeamUpdateRequest;
import com.leetao.usercenter.model.vo.TeamUserVO;
import com.leetao.usercenter.model.vo.UserVO;
import com.leetao.usercenter.service.TeamService;
import com.leetao.usercenter.mapper.TeamMapper;
import com.leetao.usercenter.service.UserService;
import com.leetao.usercenter.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
* @author taoLi
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-02-22 22:46:03
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

	@Resource
	private UserTeamService userTeamService;

	@Resource
	private UserService userService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public long addTeam(Team team, User loginUser) {
		//检验队伍是否为空
		if(team == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		//校验是否登录
		if(loginUser == null){
			throw new BusinessException(ErrorCode.NOT_LOGIN);
		}
		//对参数进行校验
		//队伍人数>1且<=20
		int num = Optional.ofNullable(team.getMaxNum()).orElse(0);
		if(num < 1 || num > 20){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不符合要求");
		}
		//队伍标题<=20
		String name = team.getName();
		if(StringUtils.isBlank(name) || name.length() > 20){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍名称不符合要求");
		}
		//队伍描述<=512
		String description = team.getDescription();
		if(StringUtils.isNotBlank(description) && description.length() > 512){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述不符合要求");
		}
		//status状态检测，不设置默认为0-公开
		int status = Optional.ofNullable(team.getStatus()).orElse(0);
		TeamStatus statusByValue = TeamStatus.getStatusByValue(status);
		if(statusByValue == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态设置错误");
		}
		String password = team.getPassword();
		//如果队伍状态为加密装状态，那么，密码必须存在，并且密码长度<=32
		if(statusByValue.equals(TeamStatus.SECRET)){
			if(StringUtils.isBlank(password) || password.length() > 32){
				throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码设置不正确");
			}
		}
		//超时时间>当前时间
		Date expireTime = team.getExpireTime();
		if(new Date().after(expireTime)){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍过期时间设置不正确");
		}
		//检验用户最多创建5个队伍
		long userId = loginUser.getId();
		QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("userId", userId);
		long count = this.count(queryWrapper);
		if(count >= 5){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多创建5个队伍");
		}
		//插入队伍信息到队伍表，以及插入队伍-用户关系表，这两次插入是一种事务
		team.setId(null);
		team.setUserId(userId);
		boolean res = this.save(team);
		Long teamId = team.getId();
		if(!res || teamId == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
		}
		UserTeam userTeam = new UserTeam();
		userTeam.setUserId(userId);
		userTeam.setTeamId(team.getId());
		userTeam.setJoinTime(new Date());
		res = userTeamService.save(userTeam);
		if(!res){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
		}

		return teamId;
	}

	@Override
	public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
		QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
		if(teamQuery != null){
			//编写查询条件
			Long id = teamQuery.getId();
			if(id != null && id > 0){
				queryWrapper.eq("id",id);
			}
			List<Long> idList = teamQuery.getIdList();
			if(CollectionUtils.isNotEmpty(idList)){
				queryWrapper.in("id",idList);
			}
			//根据关键词进行查询，就是队伍名称或者描述中存在该关键词，是一种或的关系
			String searchText = teamQuery.getSearchText();
			if(StringUtils.isNotBlank(searchText)){
				queryWrapper.and(qw->qw.like("name",searchText).or().like("description",searchText));
			}
			String name = teamQuery.getName();
			if(StringUtils.isNotBlank(name)){
				queryWrapper.like("name",name);
			}
			String description = teamQuery.getDescription();
			if(StringUtils.isNotBlank(description)){
				queryWrapper.like("description",description);
			}
			//根据最大人数查询
			Integer maxNum = teamQuery.getMaxNum();
			if(maxNum != null && maxNum > 0){
				queryWrapper.eq("maxNum",maxNum);
			}
			//根据创始人查询
			Long userId = teamQuery.getUserId();
			if(userId != null && userId > 0){
				queryWrapper.eq("userId",userId);
			}
			//根据队伍状态查询
			//只有管理员才能够查询私有或者加密房间
			Integer status = teamQuery.getStatus();
			TeamStatus teamStatus = TeamStatus.getStatusByValue(status);
			if(teamStatus == null){
				teamStatus = TeamStatus.PUBLIC;
			}
			if(!isAdmin && !teamStatus.equals(TeamStatus.PUBLIC)){
				throw new BusinessException(ErrorCode.NO_AUTH,"没有查询权限");
			}
			queryWrapper.eq("status",teamStatus.getValue());
		}
		//不显示已经过期的队伍
		queryWrapper.and(qw->qw.gt("expireTime",new Date()).or().isNull("expireTime"));
		List<Team> teamList = this.list(queryWrapper);
		//关联查询用户信息，查询加入队伍的人
		//查询队伍和创建人的信息
		//select * from team t left join user u on t.userId = u.id
		//查询队伍和已经加入队伍的成员的信息
		//select * from team t
		// join user_team ut on t.id = ut.teamId
		// join user u on ut.userId = u.id

		//在业务逻辑层面实现对队伍以及队伍创始人的查询
		if(CollectionUtils.isEmpty(teamList)){
			return new ArrayList<>();
		}
		List<TeamUserVO> voList = new ArrayList<>();
		for(Team team : teamList){
			Long userId = team.getUserId();
			if(userId == null){
				continue;
			}
			User user = userService.getById(userId);
			TeamUserVO teamUserVO = new TeamUserVO();
			BeanUtils.copyProperties(team,teamUserVO);
			if(user != null){
				UserVO userVO = new UserVO();
				BeanUtils.copyProperties(user,userVO);
				teamUserVO.setCreateUser(userVO);
			}
			voList.add(teamUserVO);
		}
		return voList;
	}

	@Override
	public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
		if(teamUpdateRequest == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Long id = teamUpdateRequest.getId();
		if(id == null || id <= 0){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		//查询队伍是否存在
		Team oldTeam = this.getById(id);
		if(oldTeam == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
		}
		//只有管理员或者队长才能够修改队伍信息
		if(oldTeam.getUserId().longValue() != loginUser.getId().longValue() && !userService.isAdmin(loginUser)){
			throw new BusinessException(ErrorCode.NO_AUTH,"无修改队伍信息权限");
		}
		//如果需要修改为加密状态，那么必须有密码
		TeamStatus teamStatus = TeamStatus.getStatusByValue(teamUpdateRequest.getStatus());
		if(teamStatus.equals(TeamStatus.SECRET)){
			if(StringUtils.isBlank(teamUpdateRequest.getPassword())){
				throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密房间需要密码");
			}
		}
		Team team = new Team();
		BeanUtils.copyProperties(teamUpdateRequest,team);
		return this.updateById(team);
	}

	@Override
	public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
		if(teamJoinRequest == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		//用户最多加入5个队伍
		Long userId = loginUser.getId();
		QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("userId",userId);
		long count = userTeamService.count(queryWrapper);
		if(count >= 5){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多加入5个队伍");
		}
		//队伍必须存在
		Long teamId = teamJoinRequest.getTeamId();
		Team team = getTeam(teamId);
		//加入队伍不能过期
		Date expireTime = team.getExpireTime();
		if(expireTime != null && expireTime.before(new Date())){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已过期");
		}
		//不能加入私有队伍
		Integer status = team.getStatus();
		TeamStatus teamStatus = TeamStatus.getStatusByValue(status);
		if(TeamStatus.PRIVATE.equals(teamStatus)){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"不能加入私有房间");
		}
		//加密队伍必须密码一致
		String password = teamJoinRequest.getPassword();
		if(TeamStatus.SECRET.equals(teamStatus)){
			if(StringUtils.isBlank(password) || !password.equals(team.getPassword())){
				throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
			}
		}
		//加入队伍人数不能超过最大人数
		long joinTeamNum = countJoinUserByTeamId(teamId);
		if(joinTeamNum >= team.getMaxNum()){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已满");
		}
		//用户不能加入已经加入的队伍
		queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("teamId",teamId);
		queryWrapper.eq("userId",userId);
		long hasjoin = userTeamService.count(queryWrapper);
		if(hasjoin >= 1){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"不能重复加入相同队伍");
		}
		//添加加入队伍信息
		UserTeam userTeam = new UserTeam();
		userTeam.setUserId(userId);
		userTeam.setTeamId(teamId);
		userTeam.setJoinTime(new Date());
		boolean res = userTeamService.save(userTeam);
		return res;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
		if(teamQuitRequest == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		//检验队伍是否存在
		Long teamId = teamQuitRequest.getTeamId();
		Team team = getTeam(teamId);
		//检验用户是否已经加入队伍
		Long userId = loginUser.getId();
		QueryWrapper<UserTeam> queryWrapper = new QueryWrapper();
		queryWrapper.eq("teamId",teamId);
		queryWrapper.eq("userId",userId);
		long count = userTeamService.count(queryWrapper);
		if(count == 0){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"未加入队伍");
		}
		long teamHasJoinNum = this.countJoinUserByTeamId(teamId);
		if(teamHasJoinNum == 1){
			//队伍中只有一人，解散队伍
			this.removeById(teamId);
			queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("teamId",teamId);
			return userTeamService.remove(queryWrapper);
		}else {
			//队伍中有多人
			//如果是队长退出队伍
			if(team.getUserId().longValue() == userId.longValue()){
				//队长退出队伍，需要将队长移动到最早加入队伍的成员
				//只需要查询user_team中的两条记录
				queryWrapper = new QueryWrapper<>();
				queryWrapper.eq("teamId",teamId);
				//拼接sql语句，根据id升序排列并去除两条数据
				queryWrapper.last("order by id asc limit 2");
				List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
				if(CollectionUtils.isEmpty(userTeamList) || userTeamList.size() < 2){
					throw new BusinessException(ErrorCode.SYSTEM_ERROR);
				}
				UserTeam nextUserTeam = userTeamList.get(1);
				Long nextUserTeamId = nextUserTeam.getUserId();
				//设置队伍的队长id
				Team updateTeam = new Team();
				updateTeam.setId(teamId);
				updateTeam.setUserId(nextUserTeamId);
				boolean res = this.updateById(updateTeam);
				if(!res){
					throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新队长失败");
				}
				//移除加入队伍的关系
				queryWrapper = new QueryWrapper<>();
				queryWrapper.eq("teamId",teamId);
				queryWrapper.eq("userId",userId);
			}
			return userTeamService.remove(queryWrapper);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteTeam(long teamId, User loginUser) {
		if(teamId <= 0){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		//检验队伍是否存在
		Team team = getTeam(teamId);
		//检验是否是队长
		if(team.getUserId().longValue() != loginUser.getId().longValue()){
			throw new BusinessException(ErrorCode.FORBIDDEN,"不是队长");
		}
		//移除所有关联关系
		QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("teamId",teamId);
		boolean res = userTeamService.remove(queryWrapper);
		if(!res){
			throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
		}
		//删除队伍
		return this.removeById(teamId);
	}

	/**
	 * 检验队伍是否存在，
	 * @param teamId 队伍id
	 * @return 返回队伍信息
	 */
	private Team getTeam(Long teamId) {
		if(teamId == null || teamId <= 0){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Team team = this.getById(teamId);
		if(team == null){
			throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
		}
		return team;
	}

	/**
	 * 获取队伍中加入队伍人数
	 * @param teamId 队伍id
	 * @return 返回队伍中人数
	 */
	private long countJoinUserByTeamId(long teamId){
		QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("teamId",teamId);
		return userTeamService.count(queryWrapper);
	}

}




