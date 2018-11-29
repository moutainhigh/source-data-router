package com.globalegrow.common.hbase;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractHbaseDataPersistence {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected Configuration configuration;

	private Connection connection;

	@PostConstruct
	public void init() throws IOException {
		connection = ConnectionFactory.createConnection(this.configuration);
	}

	public void insertData(String tableName, Map<String, Object> data, String rowKey, String columnFamily) {
		try {
			try (Table table = connection.getTable(TableName.valueOf(tableName));) {
				Put put = new Put(Bytes.toBytes(rowKey));
				data.forEach((k, v) -> {
					put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(k), Bytes.toBytes(String.valueOf(v)));
				});
				// 默认自动提交
				table.put(put);
			}
		} catch (Exception e) {
			logger.error("表数据转换出错, table data: {}", data, e);
		}
	}

	public void updateData(String tableName, Map<String, Object> data, String rowKey, String columnFamily) {
		this.insertData(tableName, data, rowKey, columnFamily);
	}

	public void deleteByRowKey(String tableName, String rowKey) {
		try {
			// Map<String, Object> dataMap = JacksonUtil.readValue(data, Map.class);
			try (Table table = connection.getTable(TableName.valueOf(tableName));) {
				Delete delete = new Delete(Bytes.toBytes(rowKey));
				// 默认自动提交
				table.delete(delete);
			}
		} catch (Exception e) {
			logger.error("表数据删除出错, table name: {}", tableName, e);
		}
	}

	public Object selectRowKeyFamilyColumn(String tableName, String rowKey, String qualifier, String columnFamily) {
		try {
			try (
				Table table = connection.getTable(TableName.valueOf(tableName));) {
				Get get = new Get(Bytes.toBytes(rowKey));
				get.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(qualifier));
				Result result = table.get(get);

				if (result.containsColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(qualifier))) {
					return Bytes.toString(result.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(qualifier)));
				}
				return null;
			}
		} catch (Exception e) {
			logger.error("表数据获取出错, table data: {}", e);
		}
		return null;
    }

	public Result getRecordByRowKey(String tableName, String rowKey, Integer maxVersions)
			throws IOException {
		HTable table = (HTable) connection.getTable(TableName.valueOf(tableName));
		Get get = new Get(Bytes.toBytes(rowKey));
		get.setMaxVersions(maxVersions.intValue());
		Result result = table.get(get);
		table.close();
		return result;
	}


}
