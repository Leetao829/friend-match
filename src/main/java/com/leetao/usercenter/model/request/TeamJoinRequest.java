package com.leetao.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 加入队伍请求参数
 */
@Data
public class TeamJoinRequest implements Serializable {

	private static final long serialVersionUID = -1853315330996617219L;

	/**
	 * 加入队伍id
	 */
	private Long teamId;

	/**
	 * 如果加入的是加密房间，需要房间密码
	 */
	private String password;

}
