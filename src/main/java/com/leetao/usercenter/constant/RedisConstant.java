package com.leetao.usercenter.constant;

/**
 * redis常数
 *
 * @author leetao
 */
public interface RedisConstant {

	/**
	 * 保存在推荐伙伴中的key值
	 */
	String RECOMMEND_KEY = "yupao:user:recommend:";

	/**
	 * 预热缓存分布式锁
	 */
	String PRECACHE_LOCK = "yupao:precachejob:docache:lock";
}
