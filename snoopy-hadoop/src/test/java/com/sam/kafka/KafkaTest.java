package com.sam.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class KafkaTest {

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        AdminClient adminClient = AdminClient.create(properties);

        ListTopicsResult listTopicsResult = adminClient.listTopics();
        KafkaFuture<Map<String, TopicListing>> mapKafkaFuture = listTopicsResult.namesToListings();
        Map<String, TopicListing> stringTopicListingMap = mapKafkaFuture.get();
        for (Map.Entry<String, TopicListing> entry : stringTopicListingMap.entrySet()) {
            System.out.println(entry.getValue().name());
        }

        // 这种是手动创建 //1个分区(机器)，一个副本
        NewTopic topic = new NewTopic("test", 1, (short) 1);
        adminClient.createTopics(Collections.singleton(topic));
        adminClient.close();


        KafkaTest kafkaTest = new KafkaTest();
        Map<String, Object> props = kafkaTest.consumerConfigs();
        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(props);

        ContainerProperties containerProperties = new ContainerProperties("test");
        // 当Acknowledgment.acknowledge()方法被调用即提交offset
        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        containerProperties.setMessageListener(new AcknowledgingMessageListener<String, String>() {
            @Override
            public void onMessage(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
                System.out.println("################ " + record);
                acknowledgment.acknowledge();
            }
        });
        MessageListenerContainer messageListener = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        ((KafkaMessageListenerContainer) messageListener).setBeanName("testAuto");
        messageListener.start();

        props = kafkaTest.producerConfigs();

        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(props);
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setDefaultTopic("test");
        int i = 0;
        while (true) {
            i++;
            kafkaTemplate.sendDefault("test", "foo" + i);
            kafkaTemplate.flush();
            Thread.sleep(1000);
        }

//        messageListener.stop();
    }

    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        // kafka集群地址
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        // groupId
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "testGroup");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "testClient");
        // 开启自动提交
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        // 自动提交offset到zk的时间间隔，时间单位是毫秒
//        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        // session超时设置，15秒，超过这个时间会认为此消费者挂掉，将其从消费组中移除
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        //键的反序列化方式
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        //值的反序列化方式
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        // kafka集群地址
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        // 消息发送确认方式，配合 ISR副本
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        // 消息发送重试次数
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        // 重试间隔时间
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "1000");
        //键的反序列化方式
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //值的反序列化方式
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //分区策略
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, DefaultPartitioner.class);
        return props;
    }

}
