import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.Executors;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;


public class Producter {
	private Producer<String, byte[]> inner;
    public Producter() throws Exception{
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(System.getProperty("user.dir")+File.separator+"src"+File.separator+"KafkaProducer.properties")));

        //根据KfkaProducer.properties加载配置信息
        ProducerConfig config = new ProducerConfig(properties);
        //ProducerConfig config = new ProducerConfig(properties);
        inner = new Producer<String, byte[]>(config);
        System.out.println("初始化成功");
    }
    //一条一条发送
    public void send(String topicName, byte[] message) {
        if(topicName == null || message == null){
            return;
        }
        KeyedMessage<String, byte[]> km = new KeyedMessage<String, byte[]>(topicName, message);
//        KeyedMessage<String, String> km = new KeyedMessage<String, String>(topicName,message);
        inner.send(km);
    }
    //批量发送
    public void send(String topicName, Collection<byte[]> messages) {
        if(topicName == null || messages == null){
            return;
        }
        if(messages.isEmpty()){
            return;
        }
        ArrayList<KeyedMessage<String, byte[]>> kms = new ArrayList<KeyedMessage<String, byte[]>>();
        for(byte[] entry : messages){
            KeyedMessage<String,  byte[]> km = new KeyedMessage<String,  byte[]>(topicName,entry);
            kms.add(km);
        }
        inner.send(kms);
    }
    public void close(){
        inner.close();
    }
    
    
    public static void main(String[] args) {
    	Producter producer = null;
        try{
            producer = new Producter();
            int i=0;
            while(i<10){
                StringBuffer sbMsg = new StringBuffer();
                sbMsg.append("我就是想给你发消息啊哈哈哈 傻了吧");
               // String s = new String("1111".getBytes(),"utf-8");
                Student s = new Student();
                s.setAge(10);
                s.setName("sunmeng");
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream stream = new ObjectOutputStream(bos);
                stream.writeObject(s);
                stream.flush();
                stream.close();
                
                
                byte [] ser = bos.toByteArray();
                //以KV对的形式发送
                producer.send("WebDataAsk", ser);
                i++;
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(producer != null){
                producer.close();
            }
        }
    }
}

//public class Producter {
//
//    private Producer<String,String> producer;
//
//    public static void main(String[] args) {
//        new Producter().start();
//    }
//
//    public void init(){
//        Properties props = new Properties();
//        /**
//         * 用于自举（bootstrapping ），producer只是用它来获得元数据（topic, partition, replicas）
//         * 实际用户发送消息的socket会根据返回的元数据来确定
//         */
//        props.put("metadata.broker.list", "192.168.245.131:9093");
//        /**
//         * 消息的序列化类
//         * 默认是 kafka.serializer.DefaultEncoder， 输入时 byte[] 返回是同样的字节数组
//         */
//        props.put("serializer.class", "kafka.serializer.StringEncoder");
//        /**
//         * producer发送消息后是否等待broker的ACK，默认是0
//         * 1 表示等待ACK，保证消息的可靠性
//         */
//        props.put("request.required.acks", "1");
//        ProducerConfig config = new ProducerConfig(props);
//        // 泛型参数分别表示 The first is the type of the Partition key, the second the type of the message
//        producer = new Producer<String, String>(config);
//    }
//
//    public void produceMsg(){
//        // 构建发送的消息
//        long timestamp = System.currentTimeMillis();
//        String msg = "Msg" + timestamp;
//        String topic = "DBSERVICE";  // 确保有这个topic
//        System.out.println("发送消息" + msg);
//
//        /**
//         * topic: 消息的主题
//         * key：消息的key，同时也会作为partition的key
//         * message:发送的消息
//         */
//        KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic,msg);
//
//        producer.send(data);
//    }
//
//    public void start() {
//        System.out.println("开始发送消息 ...");
//        Executors.newSingleThreadExecutor().execute(new Runnable() {
//            public void run() {
//                init();
//                int i=0;
//                while (i<100) {
//                    try {
//                        produceMsg();
//                        Thread.sleep(2000);
//                        i++;
//                    } catch (Throwable e) {
//                        if (producer != null) {
//                            try {
//                                producer.close();
//                            } catch (Throwable e1) {
//                                System.out.println("Turn off Kafka producer error! " + e);
//                            }
//                        }
//                    }
//
//                }
//
//            }
//        });
//    }
//}
