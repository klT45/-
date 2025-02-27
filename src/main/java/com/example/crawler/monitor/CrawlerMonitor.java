package com.example.crawler.monitor;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import java.util.concurrent.TimeUnit;

public class CrawlerMonitor {
    private final MetricRegistry metrics = new MetricRegistry();

    public void initJmxReporter() {
        JmxReporter reporter = JmxReporter.forRegistry(metrics)
                .inDomain("com.example.crawler")
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start();
    }

    public void recordFetch(String domain) {
        metrics.meter(MetricRegistry.name("fetches", domain)).mark();
    }
}