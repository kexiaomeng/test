package com.nari.sun.eventListen.iface;

import com.nari.sun.eventListen.EventSource;
import com.nari.sun.eventListen.EventType;

public interface EventListen {
	
	public void start();
	public void stop();
	public void onEvent(EventSource source);
	public void registeEvent(EventType type,Handler handler);
	
}
