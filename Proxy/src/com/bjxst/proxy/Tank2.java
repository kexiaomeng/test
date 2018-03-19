package com.bjxst.proxy;

public class Tank2 extends Tank{
	
	@Override
	public void move(){
		
		long start = System.currentTimeMillis();
		System.out.println("Tank moving");
		super.move();
		
		long end = System.currentTimeMillis();
		System.out.println("time:"+(start-end));
	}
	}

