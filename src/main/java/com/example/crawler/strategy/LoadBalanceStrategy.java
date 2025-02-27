package com.example.crawler.strategy;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoadBalanceStrategy implements UrlDispatchStrategy {
    private final Map<String, Integer> nodeLoad = new ConcurrentHashMap<>();

    @Override
    public String selectWorkerNode(String url, List<String> availableNodes) {
        // 增加空节点防护
        if (availableNodes == null || availableNodes.isEmpty()) {
            throw new IllegalArgumentException("Available nodes list is empty");
        }

        return availableNodes.stream()
                .min(Comparator.comparingInt(node -> nodeLoad.getOrDefault(node, 0)))
                .orElseThrow(() -> new IllegalStateException("No available nodes"));
    }

    // 负载更新方法（独立于接口定义）
    public void updateNodeLoad(String nodeId, int increment) {
        nodeLoad.merge(nodeId, increment, Integer::sum);
    }
}