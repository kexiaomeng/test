package kafkaListen.process;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.Decoder;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import kafkaListen.handler.ArchiveHandler;
import kafkaListen.interFace.IListener;
import kafkaListen.object.Archives;

public class MsgConsumer {
	
	
	
	private ConsumerConnector consumer;
    private String topic;
    private Map<String, IListener<?>> toplicMap = new HashMap<String, IListener<?>>();
    ExecutorService execut = Executors.newCachedThreadPool();
    public static void main(String[] arg) {
        new MsgConsumer().start();
    }

    public void init(){
    	
    	toplicMap.put("test", new ArchiveHandler());
        // ָ�� zookeeper �ĵ�ַ
//        String zookeeper = "192.168.245.131:2181";
//        String topic = "DBSERVICE";
//        String groupId = "test-group";
    	String zookeeper = "127.0.0.1:2181";
    	String topic = "test";
    	String groupId = "qzj";
    	

//        Properties props = new Properties();
    	Properties props = new Properties();
      
        props.put("zookeeper.connect", zookeeper);
       
        props.put("group.id", groupId);
       
        props.put("zookeeper.session.timeout.ms", "6000");
       
        props.put("zookeeper.sync.time.ms", "200");
       
        props.put("auto.commit.interval.ms", "1000");

        consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
        this.topic = topic;
    }
    public void consume() {
    	Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, 1);
    
//        Decoder<String> keyDecoder = new StringDecoder(new VerifiableProperties());
//        Decoder<String> valueDecoder = new StringDecoder(new VerifiableProperties());
//        Decoder<String> keyDecoder = new StringDecoder(new VerifiableProperties());
//        Decoder<String> valueDecoder = new StringDecoder(new VerifiableProperties());
        Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer.createMessageStreams(topicCountMap);
//        Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer.createMessageStreams(topicCountMap);
//        Map<String, List<KafkaStream<String ,String>>> messageStreams2 = consumer.createMessageStreams(topicCountMap,keyDecoder,valueDecoder);
//        KafkaStream<String,String> stream = messageStreams2.get(topic).get(0);
        
        for(KafkaStream<byte[], byte[]> stream : messageStreams.get(topic)){
        	System.out.println("启动消费者");
        	new MsgProcess(stream.iterator(), toplicMap.get(topic)).start();
        }
//        KafkaStream<byte[], byte[]> stream = messageStreams.get(topic).get(0);
//        ConsumerIterator<String,String> iterator = stream.iterator();
//        ConsumerIterator<byte[], byte[]> iterator2 = stream.iterator();
//        int i = 0;
//        while (iterator2.hasNext()) {
//            try {
//            	i++;
//                String message = iterator2.next().message();
////                sunTest.SunTest input = sunTest.SunTest.parseFrom(new ByteArrayInputStream(message));
//                
//                System.out.println(message);
//            } catch (Throwable e) {
//                System.out.println(e.getCause());
//            }
//        }

    }

    public void start() {
        System.out.println("启动消费者");
        
      
                init();
//                while (true) {
                    try {
                        consume();
                    } catch (Throwable e) {
                    	System.out.println(e);
                        if (consumer != null) {
                            try {
                                consumer.shutdown();
                            } catch (Throwable e1) {
                                System.out.println("Turn off Kafka consumer error! " + e);
                            }
                        }
//                    }
                
            
        }
    }
}
