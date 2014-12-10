package com.hisun.ics.tool.dbt.entity;

import org.apache.commons.lang.StringUtils;

public enum SortType {
	DESC("DESC"), ASC("ASC");
	private String type;

	SortType(String type) {
		this.type = type;
	}
	
	public static SortType toType(String type) {
		if(StringUtils.isBlank(type) ) {
			return ASC;
		}
		SortType[] types = SortType.values();
		for( SortType type1: types ) {
			if( type1.type.equals(type) ) {
				return type1;
			}
		}
		throw new IllegalArgumentException("invalid type:[" + type + "]");
	}
}
