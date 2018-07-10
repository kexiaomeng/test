package kafkaListen.interFace;

import java.io.IOException;
import java.io.Serializable;

import kafkaListen.util.SerialUtil;

public abstract class AbstractListen<E> implements IListener<E>{

	abstract public void handler(E obj) ;

	
	@SuppressWarnings("unchecked")
	@Override
	public void msgHandler(byte[] msg)  {
		// TODO Auto-generated method stub
		try {
			Object obj = SerialUtil.getBean(msg);
			handler((E)obj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("msg handler");
	}

}
