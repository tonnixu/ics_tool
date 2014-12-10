package com.hisun.ics.tool.dbt.entity;

/**
 * 数据库类型
 * @author syxlw
 *
 */
public enum DBType {
	DB2("DB2"), ORACLE("ORACLE"), MYSQL("MYSQL");
	private String type;

	DBType(String type) {
		this.type = type;
	}
	
	public static DBType toType(String type) {
		DBType[] types = DBType.values();
		for( DBType type1: types ) {
			if( type1.type.equals(type) ) {
				return type1;
			}
		}
		throw new IllegalArgumentException("invalid type:[" + type + "]");
	}
}
