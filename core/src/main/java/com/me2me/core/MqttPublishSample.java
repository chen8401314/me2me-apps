package com.me2me.core;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/29.
 */
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

public class MqttPublishSample {

//    private static String host = "tcp://192.168.89.211:61613";
//    private static String userName = "admin";
//    private static String passWord = "password";
//    private static MqttClient client;
//    public static void main(String[] args) {
//        for(int i = 0;i<100;i++){
//            send();
//        }
//    }
//
//    private static String topicStr = "用户推送服务";
//    private static void send() {
//        String topic        = "用户推送服务";
//        String content      = "";
//        int qos             = 2;
//        String broker       = "tcp://127.0.0.1:61613";
//        String clientId     = "test";
//        MemoryPersistence persistence = new MemoryPersistence();
//
//    public static void main(String[] args) throws MqttException {
//        //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，
//        //MemoryPersistence设置clientid的保存形式，默认为以内存保存
//        client = new MqttClient(host, "CallbackClient", new MemoryPersistence());
//        MqttConnectOptions options = new MqttConnectOptions();
//        //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
//        //这里设置为true表示每次连接到服务器都以新的身份连接
//        options.setCleanSession(true);
//
//
//
//
//
//        //设置连接的用户名
//        options.setUserName(userName);
//        //设置连接的密码
//        options.setPassword(passWord.toCharArray());
//        // 设置超时时间 单位为秒
//        options.setConnectionTimeout(10);
//        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
//        options.setKeepAliveInterval(20);
//        //回调
//        client.setCallback(new MqttCallback() {
//
//            @Override
//            public void connectionLost(Throwable throwable) {
//                // //连接丢失后，一般在这里面进行重连
//                System.out.println("connectionLost----------");
//            }
//
//            @Override
//            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
//            //subscribe后得到的消息会执行到这里面
//               // System.out.println("messageArrived----------");
//                System.out.println(s+"---"+mqttMessage.toString());
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
//            //publish后会执行到这里
//                System.out.println("deliveryComplete---------"+ iMqttDeliveryToken.isComplete());
//            }
//        });
//        //链接
//        client.connect(options);
//        //订阅
//        client.subscribe(topicStr, 1);
//        // MqttDefaultFilePersistence persistence = new MqttDefaultFilePersistence();
//
//        try {
//            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
//            MqttConnectOptions connOpts = new MqttConnectOptions();
//            connOpts.setUserName("admin");
//            connOpts.setPassword("password".toCharArray());
//            System.out.println("Connecting to broker: "+broker);
//            sampleClient.connect(connOpts);
//            System.out.println("Connected");
//            System.out.println("Publishing message: "+content);
//            // MqttMessage message = new MqttMessage(content.getBytes());
//            MqttMessage message = new MqttMessage("潘金莲大战武大郎".getBytes());
//            message.setQos(qos);
//            sampleClient.publish(topic, message);
//            System.out.println("Message published");
//            // Thread.sleep(1000000000L);
//            sampleClient.disconnect();
//            System.out.println("Disconnected");
//        } catch(MqttException me) {
//            System.out.println("reason "+me.getReasonCode());
//            System.out.println("msg "+me.getMessage());
//            System.out.println("loc "+me.getLocalizedMessage());
//            System.out.println("cause "+me.getCause());
//            System.out.println("excep "+me);
//            me.printStackTrace();
//        }
//    }

}
