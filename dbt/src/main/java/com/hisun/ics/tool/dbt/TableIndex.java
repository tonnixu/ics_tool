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
			System.out.println("ȡ��Ԫ��(" + col +"," + row + ")ʧ�ܣ�");
			e.printStackTrace();
		}
		return strValue;
	}

	/*
	 * װ�ر������ļ�
	 */
	public boolean loadTableIndex() {
		 boolean result = false;
		 Workbook rwb = null;

		 if (indexFile == null) {
			 System.out.println("δָ�����ݱ������ļ�����");
			 return result;
		 }

		 try {
			 // ��ģ���ļ�
			 rwb = Workbook.getWorkbook(new File(indexFile));
			 if (rwb == null) {
				 System.out.println("�����ݱ������ļ�ʧ�ܣ�" + indexFile);
				 return result;
			 }
			 Sheet indexSheet = rwb.getSheet("����");
			 if (indexSheet == null) {
				 System.out.println("��ȡ���������ʧ�ܣ�");
				 return result;
			 }
			 dbType = getCellValue(indexSheet, 2, 1); 
			 if (dbType == null) {
				 System.out.println("��ȡ��������������ݿ�����(1�У�2��)ʧ�ܣ�");
				 return result;
			 }
			 if( !dbType.equals("DB2") && !dbType.equals("ORACLE") && !dbType.equals("MYSQL") ) {
				 System.out.println("��ȡ��������������ݿ�����:[" + dbType + "]�Ƿ�");
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
			System.out.println("��ȡ�����ļ�ʧ�ܣ�" + indexFile);
			e.printStackTrace();
		}
		 
		return result;
	}
}