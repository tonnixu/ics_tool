package com.hisun.ics.tool.dbt.entity;


/**
 * ¡–¿‡–Õ
 * 
 * @author syxlw
 *
 */
public enum IndexType {
	PRIMARY_KEY("primary key"), INDEX("index"), UNIQUE_INDEX("unique index");
	private String type;

	IndexType(String type) {
		this.type = type;
	}
	
	public static IndexType toType(String type) {
		IndexType[] types = IndexType.values();
		for( IndexType type1: types ) {
			if( type1.type.equalsIgnoreCase(type) ) {
				return type1;
			}
		}
		throw new IllegalArgumentException("invalid type:[" + type + "]");
	}
}
