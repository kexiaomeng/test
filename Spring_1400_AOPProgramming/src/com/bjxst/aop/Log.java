package com.bjxst.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;



public class Log implements InvocationHandler {
	
	private Object target;
	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public void beforeMethod(Method m){
		System.out.println("save start....");
	}

	@Override
	public Object invoke(Object proxy, Method m, Object[] args)
			throws Throwable {
		// TODO Auto-generated method stu
		beforeMethod(m);
		m.invoke(target, args);
		return null;
	}
	
	

}
