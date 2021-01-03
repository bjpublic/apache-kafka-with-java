package com.example;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class RebalanceListener implements ConsumerRebalanceListener {
    private final static Logger logger = LoggerFactory.getLogger(RebalanceListener.class);
    private KafkaConsumer consumer;

    RebalanceListener(KafkaConsumer consumer) {
        this.consumer = consumer;
    }


    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        logger.warn("Partitions are assigned");

    }

    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        logger.warn("Partitions are revoked");
        consumer.commitSync(currentOffsets);
    }
}