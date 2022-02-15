package com.example;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

public class FilterTransformer implements Transformer<String, String, KeyValue<String, String>> {

    private ProcessorContext context;

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }


    @Override
    public KeyValue<String, String> transform(String key, String value) {
        if (value.length() > 5) {
            return new KeyValue<>(key, value + " length is more than 5");
        }
        return new KeyValue<>(key, value);

    }

    @Override
    public void close() {
    }

}