package com.hisun.ics.tool.dbt;

import com.hisun.ics.tool.dbt.entity.DBType;

public class DBProviderFactory {
	public static DBProvider getDBProvider(DBType dbType) {
		if( dbType.equals(DBType.ORACLE) ) {
			return new DBProviderOracle();
		} else if( dbType.equals(DBType.DB2) ) {
			return new DBProviderDB2();
		} else if( dbType.equals(DBType.MYSQL) ) {
			return new DBProviderMySql();
		} else {
			return null;
		}
	}
}
