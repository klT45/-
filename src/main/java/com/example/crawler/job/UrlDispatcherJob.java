package com.example.crawler.job;
import java.util.List; // 基础集合类
import com.example.crawler.strategy.UrlDispatchStrategy;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import java.time.Duration; // 用于时间间隔
import com.example.crawler.source.ZkNodeWatcherSource;
import com.example.crawler.config.KafkaConfigLoader; // 确保包路径正确
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class UrlDispatcherJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000);

        // 新版Kafka消费者
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(KafkaConfigLoader.loadKafkaProperties().getProperty("bootstrap.servers"))
                .setTopics("raw_urls")
                .setGroupId("url-dispatcher-group")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStream<String> urlStream = env.fromSource(source,
                WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(5)),
                "Kafka Source").rebalance();

        // 节点更新流
        DataStream<List<String>> workerUpdates = env.addSource(
                new ZkNodeWatcherSource()).broadcast();

        // 流关联与路由逻辑保持不变
        // ...

        env.execute("URL Dispatcher V2");
    }

    static class RoutingProcess extends KeyedProcessFunction<
            String, // Key 类型
            Tuple2<String, List<String>>, // 输入数据类型
            String> { // 输出类型

        private final UrlDispatchStrategy strategy;
        private transient ValueState<String> lastAssignedNodeState;

        public RoutingProcess(UrlDispatchStrategy strategy) {
            this.strategy = strategy;
        }

        @Override
        public void open(Configuration parameters) {
            ValueStateDescriptor<String> descriptor =
                    new ValueStateDescriptor<>("lastNode", String.class);
            lastAssignedNodeState = getRuntimeContext().getState(descriptor);
        }

        @Override
        public void processElement(
                Tuple2<String, List<String>> value, // 输入数据
                KeyedProcessFunction<String, Tuple2<String, List<String>>, String>.Context ctx,
                Collector<String> out // 输出收集器
        ) throws Exception {
            String history = lastAssignedNodeState.value();
            String targetNode = strategy.selectWorkerNode(value.f0, value.f1);
            lastAssignedNodeState.update(targetNode);
            out.collect(String.format("%s|%s", targetNode, value.f0));
        }
    }
}