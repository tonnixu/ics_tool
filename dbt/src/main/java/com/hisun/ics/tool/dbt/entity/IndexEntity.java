package com.hisun.ics.tool.dbt.entity;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

/**
 * ����
 * 
 * @author syxlw
 *
 */
public class IndexEntity {
	/**
	 * ��������
	 */
	private String name;

	/**
	 * ��������
	 */
	private IndexType type;
	
	private ArrayList<IndexColumnEntity> columns = new ArrayList<IndexColumnEntity>(); 

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IndexType getType() {
		return type;
	}

	public void setType(String type) {
		this.type = IndexType.toType(type);
	}

	public ArrayList<IndexColumnEntity> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<IndexColumnEntity> columns) {
		this.columns = columns;
	}
	
	public void addColumn(IndexColumnEntity column) {
		this.columns.add(column);
	}

	public void setType(IndexType type) {
		this.type = type;
	}

}
