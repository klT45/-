package com.example.crawler;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.api.common.functions.MapFunction;

import java.util.Properties;

public class FlinkUrlDistributor {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties producerProps = new Properties();
        producerProps.setProperty("bootstrap.servers", "localhost:9092");
        producerProps.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Properties consumerProps = new Properties();
        consumerProps.setProperty("bootstrap.servers", "localhost:9092");
        consumerProps.setProperty("group.id", "seed-url-group");
        consumerProps.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        FlinkKafkaConsumer<String> seedUrlConsumer = new FlinkKafkaConsumer<>(
                "seed-urls", new SimpleStringSchema(), consumerProps
        );

        env.addSource(seedUrlConsumer)
                .map(new MapFunction<String, String>() {
                    @Override
                    public String map(String url) throws Exception {
                        // 去重逻辑：简单实现，可根据需要扩展
                        return url.toLowerCase();
                    }
                })
                .addSink(new FlinkKafkaProducer<>(
                        "crawl-urls", new SimpleStringSchema(), producerProps
                ));

        env.execute("URL Distributor");
    }
}