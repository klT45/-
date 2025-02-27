package com.example.crawler.query;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class QueryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryService.class);
    private final TableEnvironment tableEnv;

    public QueryService() throws IOException {
        // 1. 初始化Table环境
        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .inStreamingMode()
                .build();
        this.tableEnv = TableEnvironment.create(settings);

        // 2. 加载SQL定义
        String ddl = loadHBaseDDL();
        tableEnv.executeSql(ddl);
    }

    /**
     * 加载HBase表映射DDL（优化资源加载方式）
     */
    private String loadHBaseDDL() throws IOException {
        // 方案一：使用ClassLoader（推荐，兼容JAR包）
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("sql/hbase_mapping.sql")) {
            if (inputStream == null) {
                throw new IOException("SQL文件未找到: sql/hbase_mapping.sql");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }


    }

    public TableResult queryByTimeRange(String start, String end) {
        String sql = String.format(
                "SELECT rowkey, content.raw, metadata.timestamp " +
                        "FROM crawled_data " +
                        "WHERE metadata.timestamp BETWEEN TIMESTAMP '%s' AND TIMESTAMP '%s'",
                start, end);
        try {
            return tableEnv.executeSql(sql);
        } catch (Exception e) {
            LOGGER.error("执行SQL查询失败: {}", sql, e);
            throw new RuntimeException("查询执行异常", e);
        }
    }
}