package com.example;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;

public class KStreamCountApplication {

    private final static Logger log = LoggerFactory.getLogger(KStreamCountApplication.class);

    private static String APPLICATION_NAME = "stream-count-application";
    private static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private static String TEST_LOG = "test";

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10000);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> stream = builder.stream(TEST_LOG);
        KTable<Windowed<String>, Long> countTable = stream.groupByKey()
                .windowedBy(TimeWindows.of(Duration.ofSeconds(5)))
                .count();
        countTable.toStream().foreach(((key, value) -> {
            log.info(key.key() + " is [" + key.window().startTime() + "~" + key.window().endTime() + "] count : " + value);
        }));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

    }
}