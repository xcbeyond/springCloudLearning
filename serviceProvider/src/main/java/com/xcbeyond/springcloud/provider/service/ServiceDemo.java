package com.xcbeyond.springcloud.provider.service;

import com.xcbeyond.springcloud.provider.model.User;

/**
 * 逻辑服务层
 * @author xcbeyond
 * 2018年8月7日上午11:47:04
 */
public interface ServiceDemo {
	public String messageContext();
	/**
	 * 插入用户信息
	 * @return
	 */
	public int insertUser();
	
	/**
	 *  通过userid查询
	 * @param userid
	 * @return
	 */
	public User queryUserByUserid(String userid);
	
	/**
	 * 通过userid更新username
	 * @param userid
	 * @param username
	 */
	public void updateByUserid(String userid, String username);
}
