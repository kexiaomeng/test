package com.bjsxt.service;
import java.lang.reflect.Proxy;


import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bjsxt.dao.UserDAO;
import com.bjsxt.dao.impl.UserDAOImpl;
import com.bjsxt.model.User;

import com.bjxst.aop.Log;
import org.springframework.context.ApplicationContext;



public class UserServiceTest {


	@Test
	public void testAdd() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
		
		
		UserService service = (UserService)ctx.getBean("userService");
		service.add(new User());
		
		
	}
	
	@Test
	public void testProxy(){
		UserDAO userDAO = new UserDAOImpl();
		Log liLog = new Log();
		liLog.setTarget(userDAO);
		UserDAO userDaoProxyDao = (UserDAO)Proxy.newProxyInstance(userDAO.getClass().getClassLoader(), new Class[]{UserDAO.class}, liLog);
		userDaoProxyDao.save(new User());
	}

}
