package com.example;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class QueryableStore {
    private final static Logger log = LoggerFactory.getLogger(QueryableStore.class);

    private static String APPLICATION_NAME = "global-table-query-store-application";
    private static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private static String ADDRESS_TABLE = "address";
    private static boolean initialize = false;
    private static ReadOnlyKeyValueStore<String, String> keyValueStore;

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KTable<String, String> addressTable = builder.table(ADDRESS_TABLE, Materialized.as(ADDRESS_TABLE));
        KafkaStreams streams;
        streams = new KafkaStreams(builder.build(), props);
        streams.start();

        TimerTask task = new TimerTask() {
            public void run() {
                if (!initialize) {
                    keyValueStore = streams.store(StoreQueryParameters.fromNameAndType(ADDRESS_TABLE,
                            QueryableStoreTypes.keyValueStore()));
                    initialize = true;
                }
                printKeyValueStoreData();
            }
        };
        Timer timer = new Timer("Timer");
        long delay = 10000L;
        long interval = 1000L;
        timer.schedule(task, delay, interval);
    }

    static void printKeyValueStoreData() {
        log.info("========================");
        KeyValueIterator<String, String> address = keyValueStore.all();
        address.forEachRemaining(keyValue -> log.info(keyValue.toString()));
    }
}

// $ bin/kafka-console-producer.sh --bootstrap-server my-kafka:9092 --topic address --property "parse.key=true" --property "key.separator=:"
// >wonyoung:Seoul
// >somin:Newyork
// >wonyoung:Seoul
// >somin:Newyork