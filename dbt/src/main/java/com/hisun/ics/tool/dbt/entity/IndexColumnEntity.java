package com.hisun.ics.tool.dbt.entity;

public class IndexColumnEntity {
	private ColumnEntity column;
	private SortType sortType;

	public ColumnEntity getColumn() {
		return column;
	}

	public void setColumn(ColumnEntity column) {
		this.column = column;
	}

	public SortType getSortType() {
		return sortType;
	}

	public void setSortType(SortType sortType) {
		this.sortType = sortType;
	}
	
	public void setSortType(String sortType) {
		this.sortType = SortType.toType(sortType);
	}

}
