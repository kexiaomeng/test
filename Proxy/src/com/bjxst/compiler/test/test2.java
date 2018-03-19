package com.bjxst.compiler.test;

import java.lang.reflect.Method;



public class test2 {
	public static void main(String args[]){
		
		Method[] methods = com.bjxst.proxy.Moveable.class.getMethods();
		for(Method method:methods){
			System.out.println(method);
		}
		
		
	}
}
