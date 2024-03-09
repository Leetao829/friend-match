package com.leetao.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leetao.usercenter.common.BaseResponse;
import com.leetao.usercenter.common.ErrorCode;
import com.leetao.usercenter.common.ResultUtils;
import com.leetao.usercenter.exception.BusinessException;
import com.leetao.usercenter.model.domain.Team;
import com.leetao.usercenter.model.domain.User;
import com.leetao.usercenter.model.domain.UserTeam;
import com.leetao.usercenter.model.dto.TeamQuery;
import com.leetao.usercenter.model.request.TeamAddRequest;
import com.leetao.usercenter.model.request.TeamJoinRequest;
import com.leetao.usercenter.model.request.TeamQuitRequest;
import com.leetao.usercenter.model.request.TeamUpdateRequest;
import com.leetao.usercenter.model.vo.TeamUserVO;
import com.leetao.usercenter.service.TeamService;
import com.leetao.usercenter.service.UserService;
import com.leetao.usercenter.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 编写队伍接口
 * @author leetao
 */
@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {

	@Resource
	private UserService userService;

	@Resource
	private TeamService teamService;

	@Resource
	private UserTeamService userTeamService;

	@PostMapping("/add")
	public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
		if(teamAddRequest == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		Team team = new Team();
		BeanUtils.copyProperties(teamAddRequest,team);
		long teamId = teamService.addTeam(team, loginUser);
		return ResultUtils.success(teamId);
	}

	@PostMapping("/update")
	public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request){
		if(teamUpdateRequest == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		boolean res = teamService.updateTeam(teamUpdateRequest, loginUser);
		if(!res){
			throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
		}
		return ResultUtils.success(true);
	}

	@GetMapping("/get")
	public BaseResponse<Team> getTeamById(long id){
		if(id <= 0){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Team team = teamService.getById(id);
		if(team == null){
			throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
		}
		return ResultUtils.success(team);
	}

	@GetMapping ("/list")
	public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery,HttpServletRequest request){
		if(teamQuery == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		boolean isAdmin = userService.isAdmin(request);
		List<TeamUserVO> teamList = teamService.listTeams(teamQuery, isAdmin);
		//判断查询到的队伍列表中那些是用户已经加入的
		//查询列表中的队伍id列表
		List<Long> idList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
		try {
			//从队伍-用户列表中查询哪些是用户加入的
			User loginUser = userService.getLoginUser(request);
			QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("userId",loginUser.getId());
			queryWrapper.in("teamId",idList);
			List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
			//已经加入的队伍id集合
			Set<Long> teamJoinSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
			//将加入的队伍hasJoin改为true
			teamList.forEach(team->{
				if(teamJoinSet.contains(team.getId())){
					team.setHasJoin(true);
				}
			});
		} catch (Exception e) {}
		return ResultUtils.success(teamList);
	}

	@PostMapping("/list/page")
	public BaseResponse<Page<Team>> listTeamsByPage(@RequestBody TeamQuery teamQuery){
		if(teamQuery == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Team team = new Team();
		BeanUtils.copyProperties(teamQuery,team);
		Page<Team> page = new Page<>(teamQuery.getPageNum(),teamQuery.getPageSize());
		QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
		Page<Team> teamPage = teamService.page(page, queryWrapper);
		return ResultUtils.success(teamPage);

	}

	@PostMapping("/join")
	public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest,HttpServletRequest request){
		if(teamJoinRequest == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		boolean res = teamService.joinTeam(teamJoinRequest,loginUser);
		return ResultUtils.success(res);
	}

	@PostMapping("/quit")
	public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest,HttpServletRequest request){
		if(teamQuitRequest == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		boolean res = teamService.quitTeam(teamQuitRequest,loginUser);
		return ResultUtils.success(res);
	}

	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteTeam(@RequestBody long teamId,HttpServletRequest request){
		if(teamId <= 0){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		boolean res = teamService.deleteTeam(teamId,loginUser);
		return ResultUtils.success(res);
	}

	/**
	 * 查询个人创建的队伍
	 * @param teamQuery 请求查询参数对象
	 * @param request 请求对象
	 * @return 返回本人创建队伍
	 */
	@GetMapping("/list/my")
	public BaseResponse<List<TeamUserVO>> listMyTeams(TeamQuery teamQuery,HttpServletRequest request){
		if(teamQuery == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		teamQuery.setUserId(loginUser.getId());
		List<TeamUserVO> list = teamService.listTeams(teamQuery, true);
		return ResultUtils.success(list);
	}

	/**
	 * 查询已经加入的队伍
	 * @param teamQuery 队伍查询参数对象，能够复用
	 * @param request 请求对象
	 * @return 返回查询结果
	 */
	@GetMapping("/list/myJoin")
	public BaseResponse<List<TeamUserVO>> listHasJoinTeams(TeamQuery teamQuery,HttpServletRequest request){
		if(teamQuery == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		//先查询用户已经加入的队伍，去重之后再在队伍中查询
		User loginUser = userService.getLoginUser(request);
		QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("userId",loginUser.getId());
		List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
		Map<Long, List<UserTeam>> map = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
		ArrayList<Long> idList = new ArrayList<>(map.keySet());
		teamQuery.setIdList(idList);
		List<TeamUserVO> voList = teamService.listTeams(teamQuery, true);
		return ResultUtils.success(voList);
	}

}
