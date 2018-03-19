import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.Decoder;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;

public class Consumer1 {
	
    private ConsumerConnector consumer;
    private String topic;
    public static void main(String[] arg) {
        new Consumer1().start();
    }

    public void init(){
        // ָ�� zookeeper �ĵ�ַ
//        String zookeeper = "192.168.245.131:2181";
//        String topic = "DBSERVICE";
//        String groupId = "test-group";
    	String zookeeper = "172.16.17.42:2181,172.16.17.41:2181,172.16.17.44:2181";
    	String topic = "WebDataAsk";
    	String groupId = "1";
    	

//        Properties props = new Properties();
    	Properties props = new Properties();
        /**
         * ���������
         */
        props.put("zookeeper.connect", zookeeper);
        /**
         * ��������ã� ����������������� consumer group
         */
        props.put("group.id", groupId);
        /**
         * �೤ʱ��û�з���������Ϣ��zookeeper�ͻ���Ϊ��ҵ��ˣ�Ĭ����6000
         */
        props.put("zookeeper.session.timeout.ms", "6000");
        /**
         * ��������zookeeper follower �� leader����ʱ��
         */
        props.put("zookeeper.sync.time.ms", "200");
        /**
         * ����consumer offsets�ύ��zookeeper��Ƶ�ʣ� Ĭ����60 * 1000
         */
        props.put("auto.commit.interval.ms", "1000");


        consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
        this.topic = topic;
    }
    public void consume() {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, 1);
        /**
         * createMessageStreams Ϊÿ��topic���� message stream
         */
//        Decoder<String> keyDecoder = new StringDecoder(new VerifiableProperties());
//        Decoder<String> valueDecoder = new StringDecoder(new VerifiableProperties());
        Decoder<String> keyDecoder = new StringDecoder(new VerifiableProperties());
        Decoder<String> valueDecoder = new StringDecoder(new VerifiableProperties());
        Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer.createMessageStreams(topicCountMap);
      //  Map<String, List<KafkaStream<String String>>> messageStreams2 = consumer.createMessageStreams(topicCountMap,keyDecoder,valueDecoder);
        KafkaStream<byte[],byte[]> stream = messageStreams.get(topic).get(0);
        KafkaStream<byte[], byte[]> stream2 = messageStreams.get(topic).get(0);
        ConsumerIterator<byte[],byte[]> iterator = stream.iterator();
        ConsumerIterator<byte[], byte[]> iterator2 = stream2.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            try {i++;
                byte[] message = iterator.next().message();
                ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(message));
                Student student = (Student)input.readObject();
                System.out.println("name    "+student.getName());
            } catch (Throwable e) {
                System.out.println(e.getCause());
            }
        }

    }

    public void start() {
        System.out.println("��ʼ������Ϣ...");
        Executors.newSingleThreadExecutor().execute(new Runnable() {

            public void run() {
                init();
                while (true) {
                    try {
                        consume();
                    } catch (Throwable e) {
                        if (consumer != null) {
                            try {
                                consumer.shutdown();
                            } catch (Throwable e1) {
                                System.out.println("Turn off Kafka consumer error! " + e);
                            }
                        }
                    }
                }
            }
        });
    }
}

