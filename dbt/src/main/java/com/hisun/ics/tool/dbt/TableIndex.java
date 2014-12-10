package com.hisun.ics.tool.dbt;

import java.io.File;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;


public class TableIndex {

	public String dbType = null;
	public String dataTbsName = null;
	public String indexTbsName = null;
	public String indexFile = null;
	
	public TableIndex() {
	}
	
	public boolean isDBMysql() {
		return dbType.equals("MYSQL");
	}
	
	public boolean isDBDB2() {
		return dbType.equals("DB2");
	}
	
	public boolean isDBOracle() {
		return dbType.equals("ORACLE");
	}
	
	public TableIndex(String indexFile) {
		setIndexFile(indexFile);
	}
	
	public void setIndexFile(String file) {
		indexFile = file;
//		System.out.println("indexFile=" + indexFile);
	}

	/*
	 * 取指定单元格的数据
	 */
	public static String getCellValue(Sheet sheet, int col, int row) {
		String strValue = null;
		
		try {
			Cell cell = sheet.getCell(col, row);
			if (cell != null) {
				strValue = cell.getContents();
			}
		} catch (Exception e) {
			System.out.println("取单元格(" + col +"," + row + ")失败！");
			e.printStackTrace();
		}
		return strValue;
	}

	/*
	 * 装载表索引文件
	 */
	public boolean loadTableIndex() {
		 boolean result = false;
		 Workbook rwb = null;

		 if (indexFile == null) {
			 System.out.println("未指定数据表索引文件名！");
			 return result;
		 }

		 try {
			 // 打开模板文件
			 rwb = Workbook.getWorkbook(new File(indexFile));
			 if (rwb == null) {
				 System.out.println("打开数据表索引文件失败！" + indexFile);
				 return result;
			 }
			 Sheet indexSheet = rwb.getSheet("索引");
			 if (indexSheet == null) {
				 System.out.println("读取表格【索引】失败！");
				 return result;
			 }
			 dbType = getCellValue(indexSheet, 2, 1); 
			 if (dbType == null) {
				 System.out.println("读取表格【索引】的数据库类型(1行，2列)失败！");
				 return result;
			 }
			 if( !dbType.equals("DB2") && !dbType.equals("ORACLE") && !dbType.equals("MYSQL") ) {
				 System.out.println("读取表格【索引】的数据库类型:[" + dbType + "]非法");
				 return result;
			 }
			 dataTbsName = getCellValue(indexSheet, 2, 2); 
			 if (dataTbsName == null) {
				 dataTbsName = new String("");
			 }
			 indexTbsName = getCellValue(indexSheet, 2, 3); 
			 if (indexTbsName == null) {
				 indexTbsName = new String("");
			 }
			 result = true;
		 } catch (Exception e) {
			System.out.println("读取索引文件失败！" + indexFile);
			e.printStackTrace();
		}
		 
		return result;
	}
}