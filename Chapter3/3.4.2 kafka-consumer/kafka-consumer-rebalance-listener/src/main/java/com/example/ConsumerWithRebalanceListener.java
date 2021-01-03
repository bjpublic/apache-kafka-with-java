package com.example;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

public class ConsumerWithRebalanceListener {
    private final static Logger logger = LoggerFactory.getLogger(ConsumerWithRebalanceListener.class);
    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private final static String GROUP_ID = "test-group";


    private static KafkaConsumer<String, String> consumer;
    private static Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap();

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        consumer = new KafkaConsumer<>(configs);
        consumer.subscribe(Arrays.asList(TOPIC_NAME), new RebalanceListener());
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> record : records) {
                logger.info("{}", record);
                currentOffsets.put(new TopicPartition(record.topic(), record.partition()),
                        new OffsetAndMetadata(record.offset() + 1, null));
                consumer.commitSync(currentOffsets);
            }
        }
    }

    private static class RebalanceListener implements ConsumerRebalanceListener {
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
            logger.warn("Partitions are assigned");

        }
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            logger.warn("Partitions are revoked");
            consumer.commitSync(currentOffsets);
        }
    }
}