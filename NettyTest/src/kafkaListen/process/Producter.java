package kafkaListen.process;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.Executors;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafkaListen.object.Archives;
import kafkaListen.util.SerialUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;




public class Producter {
	private Producer<String, byte[]> inner;
//	private Producer<String,String> inner;

    public Producter() throws Exception{
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(System.getProperty("user.dir")+File.separator+"src"+File.separator+"KafkaProducer.properties")));

        //���KfkaProducer.properties����������Ϣ
        ProducerConfig config = new ProducerConfig(properties);
        //ProducerConfig config = new ProducerConfig(properties);
        inner = new Producer<String, byte[]>(config);
//        inner = new Producer<String, String>(config);

        System.out.println("生产者启动");
    }
    //һ��һ������
    public void send(String topicName, byte[] message) {
//    public void send(String topicName, String message) {

        if(topicName == null || message == null){
            return;
        }
        KeyedMessage<String, byte[]> km = new KeyedMessage<String, byte[]>(topicName, message);
//        KeyedMessage<String, String> km = new KeyedMessage<String, String>(topicName,message);
        inner.send(km);
    }
    //��������
//    public void send(String topicName, Collection<byte[]> messages) {
//        if(topicName == null || messages == null){
//            return;
//        }
//        if(messages.isEmpty()){
//            return;
//        }
//        ArrayList<KeyedMessage<String, byte[]>> kms = new ArrayList<KeyedMessage<String, byte[]>>();
//        for(byte[] entry : messages){
//            KeyedMessage<String,  byte[]> km = new KeyedMessage<String,  byte[]>(topicName,entry);
//            kms.add(km);
//        }
//        inner.send(kms);
//    }
    public void close(){
        inner.close();
    }
    
    
   /* public static void main(String[] args) {
    	Producter producer = null;
        try{
            producer = new Producter();
            int i=0;
            while(i<10){
                StringBuffer sbMsg = new StringBuffer();
                sbMsg.append("helloworld");
                String s1 = new String("1111".getBytes(),"utf-8");
                Student s = new Student();
                s.setAge(10);
                s.setName("sunmeng");
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream stream = new ObjectOutputStream(bos);
                stream.writeObject(s);
                stream.flush();
                stream.close();
                
                sunTest.SunTest.Builder sun = sunTest.SunTest.newBuilder();
        		
        		sun.setEmail("sm");
        		sun.setId(1);
        		sun.setName("sunm");
        		sunTest.SunTest test = sun.build();
        		ByteArrayOutputStream out = new ByteArrayOutputStream();
        		test.writeTo(out);
                
                
                byte [] ser = out.toByteArray();
                //��KV�Ե���ʽ����
                
                Archives archives = new Archives();
                List<String> list = new ArrayList<String>();
                list.add("test");
                archives.setList(list);
                archives.setOptType((byte)1);
                archives.setTerminalAddr("testListen");
                producer.send("test", SerialUtil.getBytes(archives));
                i++;
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(producer != null){
                producer.close();
            }
        }
    }*/
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
//         * �����Ծ٣�bootstrapping ����producerֻ�����������Ԫ��ݣ�topic, partition, replicas��
//         * ʵ���û�������Ϣ��socket���ݷ��ص�Ԫ�����ȷ��
//         */
//        props.put("metadata.broker.list", "192.168.245.131:9093");
//        /**
//         * ��Ϣ�����л���
//         * Ĭ���� kafka.serializer.DefaultEncoder�� ����ʱ byte[] ������ͬ����ֽ�����
//         */
//        props.put("serializer.class", "kafka.serializer.StringEncoder");
//        /**
//         * producer������Ϣ���Ƿ�ȴ�broker��ACK��Ĭ����0
//         * 1 ��ʾ�ȴ�ACK����֤��Ϣ�Ŀɿ���
//         */
//        props.put("request.required.acks", "1");
//        ProducerConfig config = new ProducerConfig(props);
//        // ���Ͳ���ֱ��ʾ The first is the type of the Partition key, the second the type of the message
//        producer = new Producer<String, String>(config);
//    }
//
//    public void produceMsg(){
//        // �������͵���Ϣ
//        long timestamp = System.currentTimeMillis();
//        String msg = "Msg" + timestamp;
//        String topic = "DBSERVICE";  // ȷ�������topic
//        System.out.println("������Ϣ" + msg);
//
//        /**
//         * topic: ��Ϣ������
//         * key����Ϣ��key��ͬʱҲ����Ϊpartition��key
//         * message:���͵���Ϣ
//         */
//        KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic,msg);
//
//        producer.send(data);
//    }
//
//    public void start() {
//        System.out.println("��ʼ������Ϣ ...");
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
