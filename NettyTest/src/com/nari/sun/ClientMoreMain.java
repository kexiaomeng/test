package com.nari.sun;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

public class ClientMoreMain {
	
	public static void main(String[] args) throws IOException {
		
		
		
//		ExecutorService executorService = Executors.newCachedThreadPool();
//		for(int i = 0;i < 1 ;i++){
//			executorService.submit(new NettyClient("127.0.0.1", 8080));
		
		
		sunTest.SunTest.Builder sun = sunTest.SunTest.newBuilder();
		
		sun.setEmail("sm");
		sun.setId(1);
		sun.setName("sunmeng");
		sun.addNameTest("sun");
		sun.addNameTest("meng");
		sun.putMapEn("key", "value");
		sunTest.SunTest test = sun.build();
		System.out.println(sun);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		test.writeTo(out);
		
		
		byte [] arr = out.toByteArray();
		
		System.out.println(arr.length);
		
		ByteArrayInputStream in = new ByteArrayInputStream(arr);
		
		sunTest.SunTest test1 = sunTest.SunTest.parseFrom(in);
		
		System.out.println(test1.getEmail());
		System.out.println(test1.getId());
		System.out.println(test1.getMapEnMap().get("key"));
		
		}
	}
