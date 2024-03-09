package com.leetao.usercenter.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 返回给前端的队伍信息
 */
@Data
public class TeamUserVO implements Serializable {

	private static final long serialVersionUID = 7449203376711307605L;

	/**
	 * id
	 */
	private Long id;

	/**
	 * 队伍名称
	 */
	private String name;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 最大人数
	 */
	private Integer maxNum;

	/**
	 * 过期时间
	 */
	private Date expireTime;

	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * 0 - 公开，1 - 私有，2 - 加密
	 */
	private Integer status;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 *
	 */
	private Date updateTime;

	/**
	 * 创建队伍的成员信息
	 */
	private UserVO createUser;

	/**
	 * 是否已经加入队伍
	 */
	private boolean hasJoin = false;

}
