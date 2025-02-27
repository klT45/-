package com.example.crawler.strategy;  // 关键修复：添加包声明

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DomainHashStrategy implements UrlDispatchStrategy {
    @Override
    public String selectWorkerNode(String url, List<String> availableNodes) {
        // 新增空节点防护
        if (availableNodes == null || availableNodes.isEmpty()) {
            throw new IllegalArgumentException("Available nodes list is empty");
        }

        try {
            URL parsedUrl = new URL(url);
            String domain = parsedUrl.getHost();
            int hash = domain.hashCode();
            int index = Math.abs(hash % availableNodes.size());
            return availableNodes.get(index);
        } catch (MalformedURLException e) {
            // 随机选择时仍需校验节点列表
            return availableNodes.get(ThreadLocalRandom.current().nextInt(availableNodes.size()));
        }
    }
}