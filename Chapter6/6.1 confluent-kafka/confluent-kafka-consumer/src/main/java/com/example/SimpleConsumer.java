package com.example;

import com.google.gson.Gson;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class SimpleConsumer {
    private final static Logger logger = LoggerFactory.getLogger(SimpleConsumer.class);
    private final static String TOPIC_NAME = "test.log";
    private final static String BOOTSTRAP_SERVERS = "pkc-4v1gp.ap-northeast-1.aws.confluent.cloud:9092";
    private final static String SECURITY_PROTOCOL = "SASL_SSL";
    private final static String JAAS_CONFIG = "org.apache.kafka.common.security.plain.PlainLoginModule   required username=\"2MA2CQM3Y6GVATMX\"   password=\"2NxCD/cgHhdnuFtIb4xfzlkUPzt4v46ZEcVU8ej+DVa8bDkhEigD5888Gfju3ZBP\";";
    private final static String SSL_ENDPOINT = "https";
    private final static String SASL_MECHANISM = "PLAIN";
    private final static String GROUP_ID = "test-log-consumer-group";

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        configs.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SECURITY_PROTOCOL);
        configs.put(SaslConfigs.SASL_JAAS_CONFIG, JAAS_CONFIG);
        configs.put(SaslConfigs.SASL_MECHANISM, SASL_MECHANISM);
        configs.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, SSL_ENDPOINT);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);

        consumer.subscribe(Arrays.asList(TOPIC_NAME));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> record : records) {
                logger.info("record:{}", record);
            }
        }
    }
}