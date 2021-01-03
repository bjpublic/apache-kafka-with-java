package com.pipeline;

import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import java.util.Properties;

public class MetricStreams {

    private static KafkaStreams streams;

    public static void main(final String[] args) {

        Runtime.getRuntime().addShutdownHook(new ShutdownThread());

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "metric-streams-application");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "my-kafka:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> metrics = builder.stream("metric.all");
        KStream<String, String>[] metricBranch = metrics.branch(
                (key, value) -> MetricJsonUtils.getMetricName(value).equals("cpu"),
        (key, value) -> MetricJsonUtils.getMetricName(value).equals("memory")
        );

        metricBranch[0].to("metric.cpu");
        metricBranch[1].to("metric.memory");

        KStream<String, String> filteredCpuMetric = metricBranch[0]
                .filter((key, value) -> MetricJsonUtils.getTotalCpuPercent(value) > 0.5);

        filteredCpuMetric.mapValues(value -> MetricJsonUtils.getHostTimestamp(value)).to("metric.cpu.alert");

        streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }

    static class ShutdownThread extends Thread {
        public void run() {
            streams.close();
        }
    }
}
