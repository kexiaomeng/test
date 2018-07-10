package com.nari.sun.eventListen;

import com.nari.sun.eventListen.handler.TestHandler;
import com.nari.sun.eventListen.iface.EventListen;
import com.nari.sun.eventListen.sonClass.SimpleEventListen;

public class TestEvent {
	public static void main(String[] args) {
		EventListen listen = new SimpleEventListen();
		listen.registeEvent(EventType.MSG_SEND, new TestHandler());
		
		listen.start();
		
		
		EventSource source = new EventSource(EventType.MSG_RECEIVE);
		source.setAttributes("hello", "sunmeng");
		
		listen.onEvent(source);
	}
	
}
