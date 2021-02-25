package com.example;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Properties;

public class KStreamJoinKTable {

    private static String APPLICATION_NAME = "order-join-application";
    private static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private static String ADDRESS_TABLE = "address";
    private static String ORDER_STREAM = "order";
    private static String ORDER_JOIN_STREAM = "order_join";

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KTable<String, String> addressTable = builder.table(ADDRESS_TABLE);
        KStream<String, String> orderStream = builder.stream(ORDER_STREAM);

        orderStream.join(addressTable, (order, address) -> order + " send to " + address).to(ORDER_JOIN_STREAM);

        KafkaStreams streams;
        streams = new KafkaStreams(builder.build(), props);
        streams.start();

    }
}
//
//
//./kafka-console-producer.sh --bootstrap-server my-kafka:9092 --topic address --property "parse.key=true" --property "key.separator=:"
//        >wonyoung:Seoul
//        >somin:Newyork
//        >wonyoung:Seoul
//        >somin:Newyork
//
//        ./kafka-console-producer.sh --bootstrap-server my-kafka:9092 --topic order --property "parse.key=true" --property "key.separator=:"
//        >somin:cup
//        >somin:cup
//        >wonyoung:iPhone
//
//        ./kafka-console-consumer.sh --bootstrap-server my-kafka:9092 --topic order_join --from-beginning
//        cup send to Newyork
//        cup send to Newyork
//        cup send to Newyork
//        iPhone send to Busan
//
