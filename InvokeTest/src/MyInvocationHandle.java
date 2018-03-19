import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class MyInvocationHandle implements InvocationHandler{
	private Object obj;
	public MyInvocationHandle(Object t){
		this.obj = t;
	}
	public MyInvocationHandle(){
		
	}
	
	public Object bind(Object obj){
		this.obj = obj;
		return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), this);
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		System.out.println("dddddddddddddd");
		// TODO Auto-generated method stub
		Object tempObject = method.invoke(this.obj, args);
		return tempObject;
	}

}
