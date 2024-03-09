package com.leetao.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍请求体
 * @author leetao
 */
@Data
public class TeamQuitRequest implements Serializable {

	private static final long serialVersionUID = -7587786253965593813L;

	/**
	 * 退出队伍id
	 */
	private Long teamId;

}
