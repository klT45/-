CREATE TABLE crawled_data (
  rowkey STRING,
  content ROW<raw STRING>,
  metadata ROW<timestamp TIMESTAMP(3)>
) WITH (
  'connector' = 'hbase-2.2',
  'table-name' = 'crawled_data',                -- HBase表名
  'zookeeper.quorum' = 'zk1:2181,zk2:2181',      -- Zookeeper地址[2,7](@ref)
  'zookeeper.znode.parent' = '/hbase',           -- HBase根路径（可选）
  'sink.buffer-flush.max-rows' = '1000'          -- 写入性能优化参数[7](@ref)
);