package com.bjxst.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TimeHandler implements InvocationHandler{
	
	private Object t;
	public TimeHandler(Object obj){
		this.t = obj;
	}

	@Override
	public void invoke(Object obj,Method m) {
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis();
		System.out.println("Tank moving");
		try {
			m.invoke(t);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("time:"+(start-end));
	}

}
