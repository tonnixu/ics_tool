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

public class DBProviderOracle extends DBProvider {
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
			fw.println("drop sequence " + sequence.getName() + ";");
			StringBuffer buf = new StringBuffer();
			buf.append("create sequence ");
			buf.append(sequence.getName());
			buf.append(" minvalue ");
			buf.append(sequence.getMinValue());
			buf.append(" maxvalue ");
			buf.append(sequence.getMaxValue());
			buf.append(" start with ");
			buf.append(sequence.getStartWith());
			buf.append(" cache " + sequence.getCache());
			if( sequence.isCycle() ) {
				buf.append(" cycle");
			}
			buf.append(";");
			fw.println(buf.toString());
			buf.setLength(0);
		}
	}
	
	public void processTable(PrintWriter fw, ConfigInfo configInfo,
			TableEntity table) throws Exception {
		//
		fw.println("-- 表描述:" + table.getDesc());
		fw.println("drop table " + table.getName() + " cascade constraints;");
		fw.println("create table " + table.getName() + " (");
		ArrayList<ColumnEntity> columns = table.getColumns();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < columns.size(); i++) {
			buf.setLength(0);
			ColumnEntity column = columns.get(i);
			buf.append("   ");
			buf.append(column.getName());
			buf.append(" ");
			buf.append(column.getType().name());
			if (column.equals(ColumnType.NUMBER)) {
				buf.append("(");
				buf.append(column.getLength());
				if (column.getScale() != 0) {
					buf.append(",");
					buf.append(column.getScale());
				}
				buf.append(")");
			} else {
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
					if( StringUtils.isBlank(table.getIndexTBSName()) ) {
						buf.append(")");
					} else {
						buf.append(") tablespace " + table.getIndexTBSName() + "");
					}
					fw.println(buf.toString());
				}
			}
		}
		buf.setLength(0);
		if( StringUtils.isBlank(table.getDataTBSName()) ) {
			buf.append(")");
		} else {
			buf.append(") tablespace " + table.getDataTBSName());
		}
		fw.print(buf.toString());
		buf.setLength(0);

		// 分区
		PartitionEntity partition = table.getPartition();
		if( partition != null ) {
			fw.println("");
			buf.append("  partition by range (");
			buf.append(partition.getKey());
			buf.append(")");
			fw.println(buf.toString());
			buf.setLength(0);
			fw.println("(");
			ArrayList<PartitionItemEntity> partitionItems = partition.getPartitionItems();
			for( int j = 0; j < partitionItems.size(); j++ ) {
				PartitionItemEntity partitionItem = partitionItems.get(j);
				buf.append("    partition ");
				buf.append(partitionItem.getName());
				buf.append(" values less than('");
				buf.append(partitionItem.getStartValue());
				buf.append("')");
				if( StringUtils.isNotBlank(partitionItem.getDataTbsName()) ) {
					buf.append("tablespace ");
					buf.append(partitionItem.getDataTbsName());
				}
				buf.append(",");
				fw.println(buf.toString());
				buf.setLength(0);
				if( j == partitionItems.size() - 1 ) {
					buf.append("    partition PMAX values less than(MAXVALUE)");
					if( StringUtils.isNotBlank(partitionItem.getDataTbsName()) ) {
						buf.append("tablespace ");
						buf.append(partitionItem.getDataTbsName());
					}
					fw.println(buf.toString());
					buf.setLength(0);
				} 
			}
			fw.print(")");
		}
		
		buf.append(";");
		fw.println(buf.toString());

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
			if( StringUtils.isBlank(table.getIndexTBSName()) ) {
				buf.append(");");
			} else {
				buf.append(") tablespace " + table.getIndexTBSName() + ";");
			}
			fw.println(buf.toString());
		}

		// 注释
		fw.println("comment on table " + table.getName() + " is '"
				+ table.getDesc() + "';");
		for (int i = 0; i < columns.size(); i++) {
			buf.setLength(0);
			ColumnEntity column = columns.get(i);
			fw.println("comment on column " + table.getName() + "."
					+ column.getName() + " is '" + column.getDesc() + "';");
		}
	}

}
