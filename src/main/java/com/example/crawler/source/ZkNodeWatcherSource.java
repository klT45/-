package com.example.crawler.source;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import java.util.List;

public class ZkNodeWatcherSource extends RichSourceFunction<List<String>> {
    private transient ZooKeeper zk;
    private volatile boolean isRunning = true;
    private static final String ZK_PATH = "/workers";

    @Override
    public void open(Configuration parameters) throws Exception {
        // 1. 使用 Flink 的 Configuration 类
        zk = new ZooKeeper("localhost:2181", 3000, new NodeWatcher());
    }

    @Override
    public void run(SourceContext<List<String>> ctx) throws Exception {
        while (isRunning) {
            // 2. 每次获取子节点时重新注册 Watcher
            List<String> nodes = zk.getChildren(ZK_PATH, new NodeWatcher());
            ctx.collect(nodes);
            Thread.sleep(5000);
        }
    }

    @Override
    public void cancel() {
        // 3. 关闭 ZooKeeper 连接
        isRunning = false;
        try {
            if (zk != null) {
                zk.close();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // 4. 内部 Watcher 实现类
    private class NodeWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            if (event.getType() == Event.EventType.NodeChildrenChanged) {
                // 触发节点更新逻辑（例如记录日志或触发回调）
            }
        }
    }
}