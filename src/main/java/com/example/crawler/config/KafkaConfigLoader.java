package com.example.crawler.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KafkaConfigLoader {
    public static Properties loadKafkaProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = KafkaConfigLoader.class.getClassLoader()
                .getResourceAsStream("kafka-config.properties")) {
            props.load(input);
        }
        return props;
    }
}