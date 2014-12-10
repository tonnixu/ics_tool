package com.hisun.ics.tool.dbt.entity;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author syxlw
 *
 */
public class TableEntity {
	// ������
	private String desc;
	// ����
	private String name;
	// ��
	private ArrayList<ColumnEntity> columns = new ArrayList<ColumnEntity>();
	// ����
	private ArrayList<IndexEntity> indexs = new ArrayList<IndexEntity>();
	/**
	 * �Ƿ��Ƿ�����
	 */
	private boolean isPartition = false;
	/**
	 * ����
	 */
	private PartitionEntity partition = null;
	/**
	 * ���ݱ�ռ�
	 */
	private String dataTBSName;
	/**
	 * ������ռ�
	 */
	private String indexTBSName;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<ColumnEntity> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<ColumnEntity> columns) {
		this.columns = columns;
	}

	public void addColumn(ColumnEntity column) {
		this.columns.add(column);
	}

	public ColumnEntity getColumn(String name) {
		for (ColumnEntity column : columns) {
			if (StringUtils.equalsIgnoreCase(column.getName(), name)) {
				return column;
			}
		}
		return null;
	}

	public boolean containsColum(String name) {
		return getColumn(name) != null;
	}

	public ArrayList<IndexEntity> getIndexs() {
		return indexs;
	}

	public void setIndexs(ArrayList<IndexEntity> indexs) {
		this.indexs = indexs;
	}

	public void addIndex(IndexEntity index) {
		for(IndexEntity tmpIndex : indexs ) {
			if( tmpIndex.getName().equals(index.getName())) {
				throw new IllegalArgumentException("��:[" + name + "], ����:[" + index.getName() + "] �ظ�");
			}
		}
		this.indexs.add(index);
	}

	public IndexEntity getIndex(String name) {
		for (IndexEntity index : indexs) {
			if (StringUtils.equalsIgnoreCase(index.getName(), name)) {
				return index;
			}
		}
		return null;
	}

	public boolean containsIndex(String name) {
		return getIndex(name) != null;
	}

	public PartitionEntity getPartition() {
		return partition;
	}

	public void setPartition(PartitionEntity partition) {
		this.partition = partition;
	}

	public String getDataTBSName() {
		return dataTBSName;
	}

	public void setDataTBSName(String dataTBSName) {
		this.dataTBSName = dataTBSName;
	}

	public String getIndexTBSName() {
		return indexTBSName;
	}

	public void setIndexTBSName(String indexTBSName) {
		this.indexTBSName = indexTBSName;
	}

	public boolean isPartition() {
		return isPartition;
	}

	public void setPartition(boolean isPartition) {
		this.isPartition = isPartition;
	}
	
	public void setPartition(String isPartition) {
		this.isPartition = StringUtils.equals(isPartition, "��");
	}
	
	public IndexEntity getPrimaryKey() {
		for( IndexEntity index : indexs ) {
			if( index.getType().equals(IndexType.PRIMARY_KEY) ) {
				return index;
			}
		}
		return null;
	}
}
