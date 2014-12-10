package com.hisun.ics.tool.dbt.entity;

/**
 * 分区项信息
 * 
 * @author syxlw
 *
 */
public class PartitionItemEntity {
	private String name;
	private String startValue;
	private String endValue;
	private String dataTbsName;
	private String indexTbsName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartValue() {
		return startValue;
	}

	public void setStartValue(String startValue) {
		this.startValue = startValue;
	}

	public String getEndValue() {
		return endValue;
	}

	public void setEndValue(String endValue) {
		this.endValue = endValue;
	}

	public String getDataTbsName() {
		return dataTbsName;
	}

	public void setDataTbsName(String dataTbsName) {
		this.dataTbsName = dataTbsName;
	}

	public String getIndexTbsName() {
		return indexTbsName;
	}

	public void setIndexTbsName(String indexTbsName) {
		this.indexTbsName = indexTbsName;
	}

}
