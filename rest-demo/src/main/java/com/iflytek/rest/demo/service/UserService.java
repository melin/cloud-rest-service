/**
 * 
 */
package com.iflytek.rest.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import com.iflytek.edu.cloud.frame.annotation.ServiceMethod;
import com.iflytek.edu.cloud.frame.support.GrantType;
import com.iflytek.rest.demo.model.User;

/**
 * Create on @2014年7月30日 @下午4:54:06 
 * @author libinsong1204@gmail.com
 */
@RestController
public class UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@ServiceMethod(value="user.get", version="1.0.0", 
			grantType={GrantType.CLIENT, GrantType.PASSWORD})
	public User findUser(long id) {
		User user = new User();
		user.setAddress("合肥");
		user.setPhone("055163592110");
		user.setUsername("melin");
		return user;
	}
}
