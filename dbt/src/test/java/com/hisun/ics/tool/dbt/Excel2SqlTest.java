package com.hisun.ics.tool.dbt;

import java.io.PrintWriter;

import junit.framework.TestCase;

import com.hisun.ics.tool.dbt.entity.ConfigInfo;
import com.hisun.ics.tool.dbt.provider.excel.ConfigParse;
import com.hisun.ics.tool.dbt.util.DBTUtil;

public class Excel2SqlTest extends TestCase {
	public void test01() {
		System.setProperty("DBT_WORKDIR", "src/test/resources");
		Excel2Sql excel2Sql = new Excel2Sql(); 
		String tableName = "PUBPLTINF";
		String sqlFilePath = excel2Sql.getWorkdir() + "/sql/cre_PUBPLTINF.sql";
		String excelFilePath = excel2Sql.getWorkdir() + "/excel/表结构_" + tableName + ".xls";
		Excel2Sql testJxl = Excel2Sql.getInstance();
		boolean flag = testJxl.createSql(tableName, excelFilePath, sqlFilePath);
		if (flag) {
			System.out.println("生成数据表[ " + tableName + " ]SQL脚本成功.");
		}
	}
	
	public void test02() throws Exception {
		System.setProperty("DBT_WORKDIR", "src/test/resources");
		String tableName = "PUBPLTINF";
		ConfigInfo configInfo = ConfigParse.parse(DBTUtil.getWorkdir() + "/数据表索引.xls");
		configInfo.setDbType("MYSQL");
		DBProvider dbProvider = DBProviderFactory.getDBProvider(configInfo.getDbType());
		String sqlFilNam = DBTUtil.getWorkdir() + "/sql/cre_" + configInfo.getAppNam() + ".sql";
		PrintWriter fw = new PrintWriter(sqlFilNam);
		dbProvider.process(fw, configInfo);
		fw.close();
	}

}
