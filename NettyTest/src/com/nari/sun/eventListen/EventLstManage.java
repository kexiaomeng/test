package com.nari.sun.eventListen;

import java.util.Map;


public class EventLstManage {
	
	public static volatile EventLstManage eventLstManage;
	
	public EventLstManage getInstance(){
		if(eventLstManage == null){
			synchronized (this) {
				EventLstManage tmp = eventLstManage;
				if(tmp == null){
					tmp = new EventLstManage();
					eventLstManage = tmp;
				}
			}
		}
		return eventLstManage;
	}
}
