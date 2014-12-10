package com.hisun.ics.tool.dbt.entity;

import java.util.ArrayList;

/**
 * 分区信息
 * 
 * @author syxlw
 *
 */
public class PartitionEntity {
	private String key;
	private String statement;
	private ArrayList<PartitionItemEntity> partitionItems = new ArrayList<PartitionItemEntity>();
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public ArrayList<PartitionItemEntity> getPartitionItems() {
		return partitionItems;
	}
	public void setPartitionItems(ArrayList<PartitionItemEntity> partitionItems) {
		this.partitionItems = partitionItems;
	}
	
	public void addPartitionItem(PartitionItemEntity partitionItem) {
		this.partitionItems.add(partitionItem);
	}
	public String getStatement() {
		return statement;
	}
	public void setStatement(String statement) {
		this.statement = statement;
	}
	
}
