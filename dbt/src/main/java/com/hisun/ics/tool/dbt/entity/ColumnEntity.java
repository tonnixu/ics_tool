package com.hisun.ics.tool.dbt.entity;

import org.apache.commons.lang.StringUtils;

/**
 * 列
 * 
 * @author syxlw
 *
 */
public class ColumnEntity {
	/**
	 * 字段名
	 */
	private String name;
	/**
	 * 字段描述
	 */
	private String desc;
	/**
	 * 字段类型
	 */
	private ColumnType type;

	/**
	 * 字段长度
	 */
	private int length;

	/**
	 * 字段精度
	 */
	private int scale;

	/**
	 * 是否为空
	 */
	private boolean isNullable;
	/**
	 * 默认值
	 */
	private String defaultValue;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public ColumnType getType() {
		return type;
	}

	public void setType(String type) {
		this.type = ColumnType.toType(type);
	}
	
	public void setType(ColumnType type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public boolean isNullable() {
		return isNullable;
	}

	public void setNullable(boolean isNullable) {
		this.isNullable = isNullable;
	}
	
	public void setNullable(String isNullable) {
		if( StringUtils.equalsIgnoreCase(isNullable, "NOT NULL") ) {
			this.isNullable = true;
		} else {
			this.isNullable = false;
		}
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

}
