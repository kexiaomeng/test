package com.bjsxt.dao.impl;

import com.bjsxt.dao.UserDAO;
import com.bjsxt.model.User;
import com.bjsxt.dao.impl.UserDAOImpl;
import com.bjxst.aop.Log;

public class UserDAOImpl3 implements UserDAO{
	
	private UserDAO userDAO = new UserDAOImpl(); 
	@Override
	public void save(User user) {
		
		System.out.println("save start...");
		//new Log().beforeMethod();
		//super.save(user);
		userDAO.save(user);
	}
}
