package com.example.crawler.fetcher;

import java.time.Duration;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.Response;
import org.asynchttpclient.proxy.ProxyServer;
import com.example.crawler.proxy.ProxyManager;
import java.util.concurrent.CompletableFuture;

public class AsyncPageFetcher {
    private final ProxyManager proxyManager;
    private final AsyncHttpClient client;

    public AsyncPageFetcher() {
        this.proxyManager = new ProxyManager();
        String[] currentProxy = proxyManager.getNextProxy();

        // 新版配置采用Duration参数（网页1、5配置升级）
        DefaultAsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder()
                .setProxyServer(new ProxyServer.Builder(currentProxy[0],
                        Integer.parseInt(currentProxy[1]))
                        .build())
                .setConnectTimeout(Duration.ofMillis(5000)) // 修正为时间单位
                .setRequestTimeout(Duration.ofSeconds(10))  // 新增请求超时配置
                .setReadTimeout(Duration.ofMillis(8000))   // 新增读取超时配置
                .setMaxConnections(100)                   // 连接池优化（网页3建议）
                .build();

        this.client = Dsl.asyncHttpClient(config);
    }

    public CompletableFuture<Response> fetch(String url) {
        return client.prepareGet(url)
                .execute()
                .toCompletableFuture()
                .exceptionally(ex -> {
                    proxyManager.rotateProxy();    // 代理失效时切换（网页2重试策略）
                    throw new RuntimeException("请求失败，已切换代理", ex);
                });
    }
}