package com.example.crawler;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class SeedUrlManager {

    private static Connection connection;

    static {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "localhost");
        config.set("hbase.zookeeper.property.clientPort", "2181");
        try {
            connection = ConnectionFactory.createConnection(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addSeedUrl(String url) throws IOException {
        TableName tableName = TableName.valueOf("seed_urls");
        try (Table table = connection.getTable(tableName)) {
            Put put = new Put(Bytes.toBytes(url));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("url"), Bytes.toBytes(url));
            table.put(put);
        }
    }

    public void deleteSeedUrl(String url) throws IOException {
        TableName tableName = TableName.valueOf("seed_urls");
        try (Table table = connection.getTable(tableName)) {
            Delete delete = new Delete(Bytes.toBytes(url));
            table.delete(delete);
        }
    }

    public boolean isUrlExists(String url) throws IOException {
        TableName tableName = TableName.valueOf("seed_urls");
        try (Table table = connection.getTable(tableName)) {
            Get get = new Get(Bytes.toBytes(url));
            Result result = table.get(get);
            return !result.isEmpty();
        }
    }

    public void batchImportUrls(String[] urls) throws IOException {
        TableName tableName = TableName.valueOf("seed_urls");
        try (Table table = connection.getTable(tableName)) {
            for (String url : urls) {
                if (!isUrlExists(url)) {
                    addSeedUrl(url);
                }
            }
        }
    }
}