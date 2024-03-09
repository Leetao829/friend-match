package com.leetao.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 请求页面查询
 *
 * @author leetao
 */
@Data
public class PageRequest implements Serializable {

	private static final long serialVersionUID = 2751039691259328460L;

	/**
	 * 页面大小
	 */
	protected int pageSize = 10;

	/**
	 * 显示第几页
	 */
	protected int pageNum = 1;
}
