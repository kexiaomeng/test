import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.cluster.Broker;
import kafka.common.TopicAndPartition;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.OffsetRequest;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.TopicMetadataResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.Message;
import kafka.message.MessageAndOffset;

public class lowLevelConsumer {
	
	public static void main(String args[]){
		new lowLevelConsumer().consumer();  
		 
	}
	public void consumer(){
		int partition = 0;
		Broker broker = findLeader("192.168.245.131:9093", "DBSERVICE", partition); 
		SimpleConsumer simpleConsumer = new SimpleConsumer(broker.host(), broker.port(), 1000, 64*1024, "lowLevelConsumer");
		long startoff = 10;
		int fetchSize =1000;
		while(true){
			long offset = startoff;
			FetchRequest request = new FetchRequestBuilder().addFetch("DBSERVICE", 0, offset, fetchSize).build();
			FetchResponse response = simpleConsumer.fetch(request);
//			ByteBufferMessageSet messageSet = fetchResponse.messageSet(KafkaProperties.TOPIC, partition);  
//            for (MessageAndOffset messageAndOffset : messageSet) {  
//                Message mess = messageAndOffset.message();  
//                ByteBuffer payload = mess.payload();  
//                byte[] bytes = new byte[payload.limit()];  
//                payload.get(bytes);  
//                String msg = new String(bytes);  
//  
//  
//                offset = messageAndOffset.offset();  
//                System.out.println("partition : " + 3 + ", offset : " + offset + "  mess : " + msg);  
//            }  
//            // 继续消费下一批  
//            startOffet = offset + 1;  
			ByteBufferMessageSet messageSet = response.messageSet("DBSERVICE", partition);
			for(MessageAndOffset messageAndOffset : messageSet){
				Message mess = messageAndOffset.message();
				ByteBuffer payload = mess.payload();
				byte[] bytes = new byte[payload.limit()];
				payload.get(bytes);
//				Charset charset = Charset.forName("UTf-8");
//				
//				CharBuffer msg = charset.decode(payload);
				StringBuffer buffer = new StringBuffer();
				try {
					buffer.append(new String(bytes, 0, bytes.length, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				offset = messageAndOffset.offset();
				System.out.println("partition : " + partition + ", offset : " + offset + "  mess : " + buffer.toString()); 
        }  
			startoff = offset + 1;  
	}
	}
	public Broker findLeader(String brokerList,String topic,int partition){
		Broker leader = findpartition(brokerList,topic,partition).leader();
		System.out.println(String.format("Leader tor topic %s, partition %d is %s:%d", topic, partition, leader.host(),  
                leader.port()));  
        return leader;  
		
    }  
	private PartitionMetadata findpartition(String brokerList, String topic, int partition) {
		// TODO Auto-generated method stub
		PartitionMetadata metadata = null;
		for(String broker : brokerList.split(",")){
			SimpleConsumer consumer = null;
			String[] spiltString = broker.split(":");
			consumer = new SimpleConsumer(spiltString[0], Integer.valueOf(spiltString[1]), 1000, 64*1024, "sunmeng");
			List<String> topicList = Collections.singletonList(topic);
			TopicMetadataRequest request = new TopicMetadataRequest(topicList);
			TopicMetadataResponse response = consumer.send(request);
			List<TopicMetadata> topicMetadatas = response.topicsMetadata();
			for(TopicMetadata topicMetadata : topicMetadatas){
				for(PartitionMetadata partitionMetadata : topicMetadata.partitionsMetadata()){
					if(partitionMetadata.partitionId() == partition){
						metadata = partitionMetadata;
					}
				}
			}
			if(consumer != null){
				consumer.close();
			}
			
			
		}
		return metadata;
	}
}
