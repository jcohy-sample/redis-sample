package com.jcohy.sample.redis.bloomfilter.guava;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description:
 *
 * @author jiac
 * @version 2023.0.1 2023/8/23:17:27
 * @since 2023.0.1
 */
@Service
public class UserServiceImpl implements UserService {

	@Override
	public List<User> findAllUser() {
		return null;
	}

	@Override
	public User getUserByName(String name) {
		return null;
	}

}
