package com.example;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import java.util.Map;

public class SingleFileSinkConnectorConfig extends AbstractConfig {

    public static final String DIR_FILE_NAME = "file";
    private static final String DIR_FILE_NAME_DEFAULT_VALUE = "/tmp/kafka.txt";
    private static final String DIR_FILE_NAME_DOC = "저장할 디렉토리와 파일 이름";

    public static ConfigDef CONFIG = new ConfigDef().define(DIR_FILE_NAME,
                                                    Type.STRING,
                                                    DIR_FILE_NAME_DEFAULT_VALUE,
                                                    Importance.HIGH,
                                                    DIR_FILE_NAME_DOC);

    public SingleFileSinkConnectorConfig(Map<String, String> props) {
        super(CONFIG, props);
    }
}
