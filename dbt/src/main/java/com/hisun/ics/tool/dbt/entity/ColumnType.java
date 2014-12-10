package com.hisun.ics.tool.dbt.entity;


/**
 * ¡–¿‡–Õ
 * @author syxlw
 *
 */
public enum ColumnType {
	VARCHAR("VARCHAR"), CHAR("CHAR"), NUMBER("NUMBER"), CLOB("CLOB");
	private String type;

	ColumnType(String type) {
		this.type = type;
	}
	
	public static ColumnType toType(String type) {
		ColumnType[] types = ColumnType.values();
		for( ColumnType type1: types ) {
			if( type1.type.equals(type) ) {
				return type1;
			}
		}
		throw new IllegalArgumentException("invalid type:[" + type + "]");
	}
}
