package com.hisun.ics.tool.dbt.entity;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

/**
 * 配置信息
 * 
 * @author syxlw
 *
 */
public class ConfigInfo {
	/**
	 * 数据表空间
	 */
	private String dataTBSName;
	/**
	 * 索引表空间
	 */
	private String indexTBSName;
	/**
	 * 数据库类型
	 */
	private DBType dbType;
	/**
	 * 应用名
	 */
	private String appNam;
	
	private ArrayList<TableEntity> tables = new ArrayList<TableEntity>(); 
	private ArrayList<SequenceEntity> sequences = new ArrayList<SequenceEntity>();
	
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

	public DBType getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = DBType.toType(dbType);
	}

	public ArrayList<TableEntity> getTables() {
		return tables;
	}

	public void setTables(ArrayList<TableEntity> tables) {
		this.tables = tables;
	}

	public void addTable(TableEntity table) {
		this.tables.add(table);
	}
	
	public TableEntity getTable(String name) {
		for( TableEntity table : tables ) {
			if( StringUtils.equalsIgnoreCase(table.getName(), name) ) {
				return table;
			}
		}
		return null;
	}
	
	public boolean containsTable(String name) {
		return getTable(name) != null;
	}

	public ArrayList<SequenceEntity> getSequences() {
		return sequences;
	}

	public void setSequences(ArrayList<SequenceEntity> sequences) {
		this.sequences = sequences;
	}
	
	public void addSequence(SequenceEntity sequence) {
		this.sequences.add(sequence);
	}
	
	public SequenceEntity getSequence(String name) {
		for( SequenceEntity sequence : sequences ) {
			if( StringUtils.equalsIgnoreCase(sequence.getName(), name) ) {
				return sequence;
			}
		}
		return null;
	}
	
	public boolean containsSequence(String name) {
		return getSequence(name) != null;
	}

	public String getAppNam() {
		return appNam;
	}

	public void setAppNam(String appNam) {
		this.appNam = appNam;
	}
	
}
