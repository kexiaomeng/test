import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public class ThreadFactoryTest implements ThreadFactory{
	
	private final AtomicInteger threadNum = new AtomicInteger(1);
	private static final AtomicInteger poolNum = new AtomicInteger(1);
	private final String name;
	private final ThreadGroup group;
	
	
	public ThreadFactoryTest(){
		SecurityManager s =  System.getSecurityManager();
		group = (s == null) ? Thread.currentThread().getThreadGroup(): s.getThreadGroup();
		name = "pool-name: "+ poolNum.getAndIncrement()+",Threadname:";
	}
	@Override
	public Thread newThread(Runnable r) {
		// TODO Auto-generated method stub
		Thread t = new Thread(group, r, name+threadNum.getAndIncrement());
		return t;
	}

}
