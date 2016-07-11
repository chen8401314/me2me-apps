package com.me2me.message.service;

import org.fusesource.hawtbuf.AsciiBuffer;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

import java.util.LinkedList;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/7/8.
 */
public class MessageSenderImpl implements MessageSender {

    @Override
    public void send() {

    }

    public static void main(String[] args) throws Exception {
        MQTT mqtt = new MQTT();
        mqtt.setHost("192.168.89.79",61613);
        mqtt.setUserName("admin");
        mqtt.setPassword("password");
        FutureConnection connection = mqtt.futureConnection();
        connection.connect().await();

        final LinkedList<Future<Void>> queue = new LinkedList<Future<Void>>();
        UTF8Buffer topic = new UTF8Buffer("live_push_message");
        Buffer msg = new AsciiBuffer("hello");
        queue.add(connection.publish(topic, msg, QoS.AT_LEAST_ONCE, false));

        // Eventually we start waiting for old publish futures to complete
        // so that we don't create a large in memory buffer of outgoing message.s
        if( queue.size() >= 1000 ) {
            queue.removeFirst().await();
        }

        queue.add(connection.publish(topic, new AsciiBuffer("SHUTDOWN"), QoS.AT_LEAST_ONCE, false));
        while( !queue.isEmpty() ) {
            queue.removeFirst().await();
        }
        connection.disconnect().await();

    }
}
