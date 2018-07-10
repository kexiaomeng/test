package kafkaListen.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.consumer.ConsumerIterator;
import kafkaListen.interFace.IListener;

public class MsgProcess extends Thread{

	private ConsumerIterator<byte[], byte[]> stream;
	private IListener<?> listener;
	private Logger logger = LoggerFactory.getLogger(MsgProcess.class);
	public MsgProcess(ConsumerIterator<byte[], byte[]> stream, IListener<?> listener){
		this.stream   = stream;
		this.listener = listener;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
			while(true){
				try {
					System.out.println("监听kafka消息");
//					while(stream.hasNext()){
						byte[] msg = stream.next().message();
						listener.msgHandler(msg);
//					}
					
				    
				} catch (Exception e) {
					// TODO: handle exception
					logger.error("",e);
				}
			}

			
	}
	
	
	
}
