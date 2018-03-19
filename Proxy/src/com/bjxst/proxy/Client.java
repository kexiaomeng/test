package com.bjxst.proxy;

public class Client {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		
		Moveable m = (Moveable)Proxy.newProxyInstance(Moveable.class,new TimeHandler(new Tank()));
		
		
		m.move();
	}

}
