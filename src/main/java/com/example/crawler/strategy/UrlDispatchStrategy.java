package com.example.crawler.strategy;

import java.util.List;

public interface UrlDispatchStrategy {
    String selectWorkerNode(String url, List<String> availableNodes);
}