package com.nari.sun.eventListen.sonClass;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



import com.nari.sun.eventListen.EventSource;
import com.nari.sun.eventListen.EventType;
import com.nari.sun.eventListen.handler.TestHandler;
import com.nari.sun.eventListen.iface.EventListen;
import com.nari.sun.eventListen.iface.Handler;

public class SimpleEventListen implements EventListen{
	
	private Map<EventType, Handler> handlers = new EnumMap<EventType, Handler>(EventType.class);
	private volatile boolean started = false;
	private ThreadPoolExecutor exeService = null;
	private LinkedBlockingDeque<EventSource> eventQuene = new LinkedBlockingDeque<EventSource>();
	
	@Override
	public void registeEvent(EventType type, Handler handler) {
		// TODO Auto-generated method stub
		this.handlers.put(type, handler);
	}
	
	@Override
	public void onEvent(EventSource es){
		this.disPatch(es);
	}
	private void disPatch(EventSource es) {
		// TODO Auto-generated method stub
		this.exeService.execute(new Task(es));
	}
	
	class Task implements Runnable{
		private EventSource es;
		public Task(EventSource es){
			this.es = es;
		}
		
		public EventSource getEventSource(){
			return es;
		}
		@Override
		public void run(){
			long beginTime = System.currentTimeMillis();
			System.out.println(beginTime);
			handlerDeal(es);
			
			long endTime   = System.currentTimeMillis();
			System.out.println(endTime);
			System.out.println("事件处理时间 :"+(endTime-beginTime));
		}
	}

	private void handlerDeal(EventSource es) {
		// TODO Auto-generated method stub
		Handler handler = handlers.get(es.getType());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(handler != null){
			handler.action(es);
		}else {
			try {
				throw new Exception("事件没有处理方法");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	public void start() {
		// TODO Auto-generated method stub
		exeService = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
		exeService.setRejectedExecutionHandler(new RejectedExecutionHandler() { //定义拒绝策略
			
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				
				eventQuene.add(((Task)r).getEventSource());  //额外处理被拒绝任务
			}
		});
		WorkReject reject = new WorkReject();
		new Thread(reject).start();
		
		
		setStarted(true);
	}   
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		if(!isStarted()){
			return;
		}
		setStarted(false);
		if(exeService != null){
			exeService.shutdown();
			try {
				exeService.awaitTermination(1000, TimeUnit.MICROSECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Thread.currentThread().interrupt();
			}

		}
		exeService = null;
		System.out.println("线程停止");

	}
	
	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}
	
	class WorkReject implements Runnable{
		@Override
		public void run(){
			System.out.println("deal reject");
		}
	}
	public static void main(String[] args) {
		EventListen listen = new SimpleEventListen();
		listen.registeEvent(EventType.MSG_SEND, new TestHandler());
		
		listen.start();
		
		
		EventSource source = new EventSource(EventType.MSG_SEND);
		source.setAttributes("hello", "sunmeng");
		
		listen.onEvent(source);
	}
}
