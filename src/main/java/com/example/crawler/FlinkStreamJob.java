package com.example.crawler;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import java.util.Properties;
import java.util.regex.Pattern;

public class FlinkStreamJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 1. 配置Kafka参数
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("group.id", "flink-consumer-group");

        // 2. 创建Kafka消费者（订阅单个Topic）
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
                "test-topic",
                new SimpleStringSchema(),
                properties
        );
        consumer.setStartFromEarliest(); // 从最早偏移量开始消费

        // 3. 添加数据源
        env.addSource(consumer).print();

        env.execute("Flink Kafka Consumer Job");
    }
}