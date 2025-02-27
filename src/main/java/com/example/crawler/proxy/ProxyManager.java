package com.example.crawler.proxy;

import java.util.concurrent.atomic.AtomicInteger;

public class ProxyManager {
    private final String[][] proxyList; // 二维数组存储host-port对
    private final AtomicInteger index = new AtomicInteger(0);

    public ProxyManager() {
        this.proxyList = new String[][]{
                {"proxy1.example.com", "8080"},
                {"proxy2.example.com", "8080"}
        };
    }

    // 返回host-port数组
    public String[] getNextProxy() {
        int currentIndex = index.getAndUpdate(i -> (i + 1) % proxyList.length);
        return proxyList[currentIndex];
    }

    // 新增获取完整代理地址方法
    public String getNextProxyAddress() {
        String[] proxy = getNextProxy();
        return proxy[0] + ":" + proxy[1];
    }

    public void rotateProxy() {
        index.incrementAndGet();
    }
}