package com.example;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

@SpringBootApplication
public class SpringConsumerApplication {
    public static Logger logger = LoggerFactory.getLogger(SpringConsumerApplication.class);


    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpringConsumerApplication.class);
        application.run(args);
    }

    @KafkaListener(topics = "test", groupId = "test-group-01")
    public void commitListener(ConsumerRecords<String, String> records, Acknowledgment ack) {
        records.forEach(record -> logger.info(record.toString()));
        ack.acknowledge();
    }

    @KafkaListener(topics = "test", groupId = "test-group-02")
    public void consumerCommitListener(ConsumerRecords<String, String> records, Consumer<String, String> consumer) {
        records.forEach(record -> logger.info(record.toString()));
        consumer.commitSync();
    }
}
