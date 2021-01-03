package com.example;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class SimpleProducer {
    private final static Logger logger = LoggerFactory.getLogger(SimpleProducer.class);
    private final static String TOPIC_NAME = "test.log";
    private final static String BOOTSTRAP_SERVERS = "pkc-4v1gp.ap-northeast-1.aws.confluent.cloud:9092";
    private final static String SECURITY_PROTOCOL = "SASL_SSL";
    private final static String JAAS_CONFIG = "org.apache.kafka.common.security.plain.PlainLoginModule   required username=\"2MA2CQM3Y6GVATMX\"   password=\"2NxCD/cgHhdnuFtIb4xfzlkUPzt4v46ZEcVU8ej+DVa8bDkhEigD5888Gfju3ZBP\";";
    private final static String SSL_ENDPOINT = "https";
    private final static String SASL_MECHANISM = "PLAIN";


    public static void main(String[] args) {

        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        configs.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SECURITY_PROTOCOL);
        configs.put(SaslConfigs.SASL_JAAS_CONFIG, JAAS_CONFIG);
        configs.put(SaslConfigs.SASL_MECHANISM, SASL_MECHANISM);
        configs.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, SSL_ENDPOINT);

        KafkaProducer<String, String> producer = new KafkaProducer<>(configs);

        String messageKey = "helloKafka";
        String messageValue = "helloConfluentCloud";
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, messageKey, messageValue);
        try {
            RecordMetadata metadata = producer.send(record).get();
            logger.info(metadata.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        } finally {
            producer.flush();
            producer.close();
        }


        for (int i = 0; i < 1000; i++) {
            String messageKey = "helloKafka";
            String messageValue = "helloConfluentCloud" + i;
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, messageKey, messageValue);
            try {
                RecordMetadata metadata = producer.send(record).get();
                logger.info(metadata.toString());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                producer.flush();
            }
        }
        producer.close();
    }
}