package com.hisun.ics.tool.dbt.util;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class ExcelUtil {
	/*
	 * ȡָ����Ԫ�������
	 */
	public static String getCellValue(Sheet sheet, int col, int row) {
		String strValue = null;

		Cell cell = sheet.getCell(col, row);
		if (cell != null) {
			strValue = cell.getContents();
		}
		return strValue;
	}

	public static void closeWorkbook(Workbook wb) {
		if (wb == null) {
			return;
		}
		wb.close();
	}
}
