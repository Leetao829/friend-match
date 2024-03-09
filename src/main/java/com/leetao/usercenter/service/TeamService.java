package com.leetao.usercenter.service;

import com.leetao.usercenter.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leetao.usercenter.model.domain.User;
import com.leetao.usercenter.model.dto.TeamQuery;
import com.leetao.usercenter.model.request.TeamJoinRequest;
import com.leetao.usercenter.model.request.TeamQuitRequest;
import com.leetao.usercenter.model.request.TeamUpdateRequest;
import com.leetao.usercenter.model.vo.TeamUserVO;
import java.util.List;

/**
* @author taoLi
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-02-22 22:46:03
*/
public interface TeamService extends IService<Team> {

	/**
	 * 用户添加队伍
	 * @param team 队伍信息
	 * @param loginUser 登录用户信息
	 * @return 返回插入是否成功
	 */
	long addTeam(Team team, User loginUser);

	/**
	 * 查询队伍列表
	 * @param teamQuery 队伍查询信息
	 * @param isAdmin 是否为管理员
	 * @return 返回脱敏之后的查询结果列表
	 */
	List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

	/**
	 * 更新队伍信息
	 * @param teamUpdateRequest 队伍更新请求参数
	 * @return 返回是否更新成功
	 */
	boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser);

	/**
	 * 加入队伍
	 * @param teamJoinRequest 加入队伍信息
	 * @return 加入队伍是否成功
	 */
	boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

	/**
	 * 退出队伍
	 * @param teamQuitRequest 请求参数
	 * @param loginUser 登录用户信息
	 * @return 退出队伍是否成功
	 */
	boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

	/**
	 * 解散队伍
	 * @param teamId 队伍id
	 * @param loginUser 解散队伍用户信息
	 * @return 返回解散是否成功
	 */
	boolean deleteTeam(long teamId, User loginUser);


}
