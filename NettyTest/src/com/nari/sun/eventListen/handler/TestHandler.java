package com.nari.sun.eventListen.handler;

import com.nari.sun.eventListen.EventSource;
import com.nari.sun.eventListen.iface.Handler;


public class TestHandler  implements Handler{

	@Override
	public void action(EventSource es){
		System.out.println(es.getType().getName()+" handler~~~~~~~~");
	}
}
