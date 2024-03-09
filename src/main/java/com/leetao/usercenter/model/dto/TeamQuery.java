package com.leetao.usercenter.model.dto;

import com.leetao.usercenter.common.PageRequest;
import lombok.Data;

import java.util.List;

@Data
public class TeamQuery extends PageRequest {
	/**
	 * id
	 */
	private Long id;

	/**
	 * 队伍id列表
	 */
	private List<Long> idList;

	/**
	 * 搜索关键词，同时对队伍名称和描述进行查询
	 */
	private String searchText;

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
	 * 用户id
	 */
	private Long userId;

	/**
	 * 0 - 公开，1 - 私有，2 - 加密
	 */
	private Integer status;
}
