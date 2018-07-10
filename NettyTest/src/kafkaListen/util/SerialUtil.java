package kafkaListen.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerialUtil {

	public static Object getBean(byte[] msg) throws IOException, ClassNotFoundException{
		
		ByteArrayInputStream in = new ByteArrayInputStream(msg);
		ObjectInputStream ob = new ObjectInputStream(in);
		Object obj = ob.readObject();
		
		ob.close();
		in.close();
		
		return obj;
	}
	
	public static byte[] getBytes(Object obj){
		
		byte[] ser = null;
		try {
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			ObjectOutputStream objOut = new ObjectOutputStream(out);
			objOut.writeObject(obj);
			objOut.flush();
			objOut.close();
			
			ser = out.toByteArray();
			out.close();
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ser;

	}
}
