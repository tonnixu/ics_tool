package com.hisun.ics.tool.dbt.provider.excel;

import jxl.Cell;
import jxl.Sheet;

import com.hisun.ics.tool.dbt.entity.ConfigInfo;

public class ExcelProvider {

	public ConfigInfo load(String tableName) throws Exception {
		ConfigInfo configInfo = ConfigParse.parse(getWorkdir() + "/���ݱ�����.xls");
		
		return configInfo;
	}

	public static String getWorkdir() {
		return System.getProperty("DBT_WORKDIR");
	}

	/*
	 * ȡָ����Ԫ�������
	 */
	public static String getCellValue(Sheet sheet, int col, int row) {
		String strValue = null;

		try {
			Cell cell = sheet.getCell(col, row);
			if (cell != null) {
				strValue = cell.getContents();
			}
		} catch (Exception e) {
			System.out.println("ȡ��Ԫ��(" + col + "," + row + ")ʧ�ܣ�");
			e.printStackTrace();
		}
		return strValue;
	}
}
