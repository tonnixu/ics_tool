package com.hisun.ics.tool.dbt.entity;

/**
 * –Ú¡–
 * 
 * @author syxlw
 *
 */
public class SequenceEntity {
	private String name;
	private long minValue = 1;
	private long maxValue = 999999999999999l;
	private long startWith = -1;
	private long incrementBy = 1;
	private boolean isCycle;
	private String desc;
	private int cache = 100;

	public int getCache() {
		return cache;
	}

	public void setCache(int cache) {
		this.cache = cache;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getMinValue() {
		return minValue;
	}

	public void setMinValue(long minValue) {
		this.minValue = minValue;
	}

	public long getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(long maxValue) {
		this.maxValue = maxValue;
	}

	public long getStartWith() {
		if( startWith == -1 ) {
			return minValue;
		}
		return startWith;
	}

	public void setStartWith(long startWith) {
		this.startWith = startWith;
	}

	public long getIncrementBy() {
		return incrementBy;
	}

	public void setIncrementBy(long incrementBy) {
		this.incrementBy = incrementBy;
	}

	public boolean isCycle() {
		return isCycle;
	}

	public void setCycle(boolean isCycle) {
		this.isCycle = isCycle;
	}

	public void setCycle(String isCycle) {
		this.isCycle = " «".equals(isCycle);
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
