package com.nari.sun.netty;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientMoreMain {
	
	public static void main(String[] args) {
		
		
		
		ExecutorService executorService = Executors.newCachedThreadPool();
		for(int i = 0;i < 10 ;i++){
			executorService.submit(new NettyClient("127.0.0.1", 8080));

		}
	}
}
