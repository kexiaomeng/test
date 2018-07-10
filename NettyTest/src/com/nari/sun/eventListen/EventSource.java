package com.nari.sun.eventListen;

import java.util.HashMap;
import java.util.Map;

public class EventSource {
	
	private EventType type;
	private final Map<String, Object> attributes = new HashMap<String, Object>();


	public EventSource(EventType type){
		this.type = type;
	}
	
	public EventType getType() {
		return type;
	}
	public Map<String, Object> getAttributes() {
		return attributes;
	} 
	
	public void setAttributes(String key,Object value){
		this.getAttributes().put(key, value);
	}
	
	public void removeAttributes(String key){
		this.getAttributes().remove(key);
	}
}
