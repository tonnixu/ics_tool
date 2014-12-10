package com.hisun.ics.tool.dbt;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.hisun.ics.tool.dbt.entity.ColumnEntity;
import com.hisun.ics.tool.dbt.entity.ColumnType;
import com.hisun.ics.tool.dbt.entity.ConfigInfo;
import com.hisun.ics.tool.dbt.entity.IndexColumnEntity;
import com.hisun.ics.tool.dbt.entity.IndexEntity;
import com.hisun.ics.tool.dbt.entity.IndexType;
import com.hisun.ics.tool.dbt.entity.PartitionEntity;
import com.hisun.ics.tool.dbt.entity.PartitionItemEntity;
import com.hisun.ics.tool.dbt.entity.SequenceEntity;
import com.hisun.ics.tool.dbt.entity.TableEntity;

public class DBProviderMySql extends DBProvider {

	@Override
	public void process(PrintWriter fw, ConfigInfo configInfo) throws Exception {
		ArrayList<TableEntity> tables = configInfo.getTables();
		fw.println("-- 自动生成 for " + configInfo.getDbType().name() + ", 时间:"
				+ DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		for (TableEntity table : tables) {
			processTable(fw, configInfo, table);
		}
		processSequences(fw, configInfo);

	}

	public void processSequences(PrintWriter fw, ConfigInfo configInfo) {
		ArrayList<SequenceEntity> sequences = configInfo.getSequences();
		for (SequenceEntity sequence : sequences) {
			fw.println("-- 序列描述：" + sequence.getDesc());
			fw.println("delete from sequence where name='" + sequence.getName()
					+ "';");
			fw.println("insert into sequence values('" + sequence.getName()
					+ "'," + sequence.getMinValue() + ","
					+ sequence.getIncrementBy() + ");");
		}
	}

	public void processTable(PrintWriter fw, ConfigInfo configInfo,
			TableEntity table) throws Exception {
		//
		fw.println("-- 表描述:" + table.getDesc());
		fw.println("drop table if exists " + table.getName() + ";");
		fw.println("create table " + table.getName() + " (");
		ArrayList<ColumnEntity> columns = table.getColumns();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < columns.size(); i++) {
			buf.setLength(0);
			ColumnEntity column = columns.get(i);
			buf.append("   ");
			buf.append(column.getName());
			buf.append(" ");
			if (column.getType().equals(ColumnType.NUMBER)) {
				if (column.getScale() != 0) {
					buf.append("DECIMAL");
					buf.append("(");
					buf.append(column.getLength());
					buf.append(",");
					buf.append(column.getScale());
					buf.append(")");
				} else {
					buf.append("BIGINT");
				}
			} else if (column.getType().equals(ColumnType.CLOB)) {
				buf.append("LONGTEXT");
				buf.append("(");
				buf.append(column.getLength());
				buf.append(")");
			} else {
				buf.append(column.getType().name());
				buf.append("(");
				buf.append(column.getLength());
				buf.append(")");
			}
			buf.append(" ");
			if (StringUtils.isNotBlank(column.getDefaultValue())) {
				buf.append("default ");
				buf.append(column.getDefaultValue());
			}
			buf.append(" ");
			if (column.isNullable()) {
				buf.append(" not null");
			}
			buf.append(" ");
			if (StringUtils.isNotBlank(column.getDesc())) {
				buf.append("comment '" + column.getDesc() + "'");
			}
			IndexEntity primaryKey = table.getPrimaryKey();
			if (i < columns.size() - 1 || primaryKey != null) {
				buf.append(",");
			}
			fw.println(buf.toString());
			if (i == columns.size() - 1) {
				// 主键
				buf.setLength(0);
				if (primaryKey != null) {
					ArrayList<IndexColumnEntity> indexColumns = primaryKey
							.getColumns();
					buf.append("   primary key(");
					for (int j = 0; j < indexColumns.size(); j++) {
						buf.append(indexColumns.get(j).getColumn().getName());
						if (j < indexColumns.size() - 1) {
							buf.append(",");
						}
					}
					buf.append(")");
					fw.println(buf.toString());
					buf.setLength(0);
				}
			}
		}
		fw.print(")");
		// 分区
		PartitionEntity partition = table.getPartition();
		if (partition != null) {
			buf.append("  partition by range (");
			buf.append(partition.getKey());
			buf.append(")");
			fw.println(buf.toString());
			fw.println("(");
			ArrayList<PartitionItemEntity> partitionItems = partition
					.getPartitionItems();
			for (int j = 0; j < partitionItems.size(); j++) {
				PartitionItemEntity partitionItem = partitionItems.get(j);
				buf.setLength(0);
				buf.append("    partition ");
				buf.append(partitionItem.getName());
				buf.append(" values less than('");
				buf.append(partitionItem.getStartValue());
				buf.append("')");
				buf.append(",");
				fw.println(buf.toString());
				buf.setLength(0);
				if (j == partitionItems.size() - 1) {
					buf.append("    partition PMAX values less than(MAXVALUE)");
					fw.println(buf.toString());
					buf.setLength(0);
					;
				}
			}
			fw.print(")");
		}

		if (StringUtils.isNotBlank(table.getDesc())) {
			fw.println(" comment='" + table.getDesc() + "';");
		} else {
			fw.println(";");
		}

		// 索引
		ArrayList<IndexEntity> indexs = table.getIndexs();
		for (IndexEntity index : indexs) {
			if (index.getType().equals(IndexType.PRIMARY_KEY)) {
				continue;
			}
			buf.setLength(0);
			if (index.getType().equals(IndexType.UNIQUE_INDEX)) {
				buf.append("create unique index ");
			} else {
				buf.append("create index ");
			}
			buf.append(index.getName());
			buf.append(" on " + table.getName());
			buf.append("(");
			ArrayList<IndexColumnEntity> indexColumns = index.getColumns();
			for (int i = 0; i < indexColumns.size(); i++) {
				IndexColumnEntity indexColumn = indexColumns.get(i);
				buf.append(indexColumn.getColumn().getName());
				buf.append(" ");
				buf.append(indexColumn.getSortType());
				if (i < indexColumns.size() - 1) {
					buf.append(",");
				}
			}
			buf.append(");");
			fw.println(buf.toString());
		}

	}

}
