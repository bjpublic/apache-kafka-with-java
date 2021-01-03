package com.example;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class MultiConsumerThreadByPartition {
    private final static Logger logger = LoggerFactory.getLogger(MultiConsumerThreadByPartition.class);

    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private final static String GROUP_ID = "test-group";

    private final static List<ConsumerWorker> workerThreads = new ArrayList<>();

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        int CONSUMER_COUNT = getPartitionSize(TOPIC_NAME);
        logger.info("Set thread count : {}", CONSUMER_COUNT);

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            ConsumerWorker worker = new ConsumerWorker(configs, TOPIC_NAME, i);
            workerThreads.add(worker);
            executorService.execute(worker);
        }
    }

    static int getPartitionSize(String topic) {
        logger.info("Get {} partition size", topic);
        int partitions;
        Properties adminConfigs = new Properties();
        adminConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        AdminClient admin = AdminClient.create(adminConfigs);
        try {
            DescribeTopicsResult result = admin.describeTopics(Arrays.asList(topic));
            Map<String, KafkaFuture<TopicDescription>> values = result.values();
            KafkaFuture<TopicDescription> topicDescription = values.get(topic);
            partitions = topicDescription.get().partitions().size();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            partitions = getDefaultPartitionSize();
        }
        admin.close();
        return partitions;
    }

    static int getDefaultPartitionSize() {
        logger.info("getDefaultPartitionSize");
        int partitions = 1;
        Properties adminConfigs = new Properties();
        adminConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        AdminClient admin = AdminClient.create(adminConfigs);
        try {
            for (Node node : admin.describeCluster().nodes().get()) {
                ConfigResource cr = new ConfigResource(ConfigResource.Type.BROKER, "0");
                DescribeConfigsResult describeConfigs = admin.describeConfigs(Collections.singleton(cr));
                Config cf = describeConfigs.all().get().get(cr);
                Optional<ConfigEntry> optionalConfigEntry = cf.entries().stream()
                        .filter(v -> v.name().equals("num.partitions")).findFirst();
                ConfigEntry numPartitionConfig = optionalConfigEntry.orElseThrow(Exception::new);
                partitions = Integer.getInteger(numPartitionConfig.value());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        admin.close();
        return partitions;
    }

    static class ShutdownThread extends Thread {
        public void run() {
            workerThreads.forEach(ConsumerWorker::shutdown);
            System.out.println("Bye");
        }
    }
}