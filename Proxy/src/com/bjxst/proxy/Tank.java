package com.bjxst.proxy;
import java.util.Random;



public class Tank implements Moveable{
	
	@Override 
	public void move(){
		
		try {
			Thread.sleep(new Random().nextInt(10000));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		System.out.println("Tank stopping.....");
	}

}
