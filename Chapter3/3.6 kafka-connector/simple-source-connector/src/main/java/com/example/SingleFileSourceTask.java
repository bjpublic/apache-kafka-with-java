package com.example;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.kafka.connect.storage.OffsetStorageReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SingleFileSourceTask extends SourceTask {
    private Logger logger = LoggerFactory.getLogger(SingleFileSourceTask.class);

    public final String FILENAME_FIELD = "filename";
    public final String POSITION_FIELD = "position";

    private Map<String, String> fileNamePartition;
    private Map<String, Object> offset;
    private String topic;
    private String file;
    private long position = -1;


    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public void start(Map<String, String> props) {
        try {
            // Init variables
            SingleFileSourceConnectorConfig config = new SingleFileSourceConnectorConfig(props);
            topic = config.getString(SingleFileSourceConnectorConfig.TOPIC_NAME);
            file = config.getString(SingleFileSourceConnectorConfig.DIR_FILE_NAME);
            fileNamePartition = Collections.singletonMap(FILENAME_FIELD, file);
            offset = context.offsetStorageReader().offset(fileNamePartition);

            // Get file offset from offsetStorageReader
            if (offset != null) {
                Object lastReadFileOffset = offset.get(POSITION_FIELD);
                if (lastReadFileOffset != null) {
                    position = (Long) lastReadFileOffset;
                }
            } else {
                position = 0;
            }

        } catch (Exception e) {
            throw new ConnectException(e.getMessage(), e);
        }
    }

    @Override
    public List<SourceRecord> poll() {
        List<SourceRecord> results = new ArrayList<>();
        try {
            Thread.sleep(1000);

            List<String> lines = getLines(position);

            if (lines.size() > 0) {
                lines.forEach(line -> {
                    Map<String, Long> sourceOffset = Collections.singletonMap(POSITION_FIELD, ++position);
                    SourceRecord sourceRecord = new SourceRecord(fileNamePartition, sourceOffset, topic, Schema.STRING_SCHEMA, line);
                    results.add(sourceRecord);
                });
            }
            return results;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ConnectException(e.getMessage(), e);
        }
    }

    private List<String> getLines(long readLine) throws Exception {
        BufferedReader reader = Files.newBufferedReader(Paths.get(file));
        return reader.lines().skip(readLine).collect(Collectors.toList());
    }

    @Override
    public void stop() {
    }
}


