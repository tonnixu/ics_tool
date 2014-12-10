package com.hisun.ics.tool.dbt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
//import jxl.*;
//import jxl.format.*;
//import jxl.write.*;
//import jxl.write.biff.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


public class Table2Excel {
	private final static Table2Excel obj = new Table2Excel();

	public static Table2Excel getInstance() {
		return obj;
	}

	public Table2Excel() {
	}

	/**
	 * FunName : writeCell
	 * Description : д��Ԫ�񣨱���ԭ��Ԫ�����ԣ�
	 * @param sheet�����
	 * @param col����Ԫ���к�
	 * @param row����Ԫ���к�
	 * @param content����Ԫ������
	 */
	public void writeCell(WritableSheet sheet, int col, int row, String content) {
		try {
			WritableCell cell = sheet.getWritableCell(col,row);
			CellFormat cf = cell.getCellFormat();
			Label lbl = new Label(col, row, content);
			lbl.setCellFormat(cf);
			sheet.addCell(lbl);
		} catch (Exception e) {
			System.out.println("д�뵥Ԫ��ʧ�ܣ�");
			e.printStackTrace();
		}
	}

	/**
	 * FunName : writeCell2
	 * Description : д��Ԫ�����Ը��Ƶڶ��еĵ�Ԫ��
	 * @param sheet�����
	 * @param col����Ԫ���к�
	 * @param row����Ԫ���к�
	 * @param content����Ԫ������
	 */
	public void writeCell2(WritableSheet sheet, int col, int row, String content) {
		try {
			WritableCell cell = sheet.getWritableCell(col,1);
			CellFormat cf = cell.getCellFormat();
			Label lbl = new Label(col, row, content);
			lbl.setCellFormat(cf);
			sheet.addCell(lbl);
		} catch (Exception e) {
			System.out.println("д�뵥Ԫ��ʧ�ܣ�");
			e.printStackTrace();
		}
	}

	/**
	 * FunName : mergeColCells
	 * Description : �ϲ�ͬһ���еĶ����Ԫ��
	 * @param sheet: ���
	 * @param col���ϲ���Ԫ�������к�
	 * @param row1����ʼ�к�
	 * @param row2�������к�
	 */
	public void mergeColCells(WritableSheet sheet, int col, int row1, int row2) {
		try {
			//�ϲ�ָ�������Ԫ��
			sheet.mergeCells(col, row1, col, row2);
			
			//�����ϲ���ı߿�
			WritableCell cell = sheet.getWritableCell(col, row1);
			CellFormat cf = cell.getCellFormat();
			Label lbl;
			for (int i=row1+1; i<=row2; i++) {
				lbl = new Label(col, i, " ");
				lbl.setCellFormat(cf);
				sheet.addCell(lbl);
			}

		} catch (Exception e) {
			System.out.println("д�뵥Ԫ��ʧ�ܣ�");
			e.printStackTrace();
		}
	}
	
	public boolean createTable(String tableName, String patternFilePath, 
				String outputFilePath, String textFilePath) {
		boolean createFlag = true;
		Workbook rwb = null;
		WritableWorkbook wwb = null;
		File file = null;

		SplitIndex indexParser = new SplitIndex();
		
		try {
			file=new File(textFilePath);
			if(!file.exists()||file.isDirectory()) {
				System.out.println("���ļ�[" + textFilePath + "]ʧ�ܣ�");
				createFlag = false;
				return createFlag;
			}
			
			// ��ģ���ļ�
			rwb = Workbook.getWorkbook(new File(patternFilePath));
			// ���ɱ�ṹ�ļ�
			wwb = Workbook.createWorkbook(new File(outputFilePath), rwb);

			//����"�޶���¼"ҳ
			WritableSheet sheet = wwb.getSheet("�޶���¼");
			if (sheet != null) {
				writeCell(sheet, 0, 2, "001");
				writeCell(sheet, 1, 2, "�Զ�����");
				Date dt=new Date();
				SimpleDateFormat matter1=new SimpleDateFormat("yyyy-MM-dd");
				writeCell(sheet, 2, 2, matter1.format(dt));
				writeCell(sheet, 4, 2, "1.0");
				writeCell(sheet, 5, 2, "����");
			}

			//����"������"ҳ
			sheet = wwb.getSheet("������");
			if (sheet != null) {
				writeCell(sheet, 1, 1, tableName);
			}

			// �����ݱ������ļ�
			BufferedReader br = new BufferedReader(new FileReader(file));
			String temp = null;
			String lastIndexName = null;
			String[] array;
			int columnSeq = 0;
			int indexSeq = 1; //0��Ϊ������
			int indexSeqLast = 1;
			int indexColCount = 0;
			
			WritableSheet columnSheet = wwb.getSheet("��ṹ");
			WritableSheet indexSheet = wwb.getSheet("����");
			if (columnSheet == null || indexSheet == null) {
				System.out.println("ģ���ʽ����");
				createFlag = false;
				return createFlag;
			}
			
	        while(true) {
		        temp = br.readLine();
		        if (temp == null)
		        	break;
		        array = temp.split(",");

				int count = array.length;
				
//				System.out.println("����=" + count);
				
				//����"��ṹ"ҳ
				String type = array[0];
				if (type.equalsIgnoreCase("COLUMN")) {
					if (count != 8){
						createFlag = false;
						System.out.println("COLUMN���ݸ�ʽ����" + temp);
						break;
					}
					columnSeq ++;
					writeCell2(columnSheet, 0, columnSeq, array[1]);
					writeCell2(columnSheet, 1, columnSeq, array[2]);
					writeCell2(columnSheet, 2, columnSeq, " ");
					writeCell2(columnSheet, 3, columnSeq, array[3]);
					writeCell2(columnSheet, 4, columnSeq, array[4]);
					writeCell2(columnSheet, 5, columnSeq, array[5]);
					writeCell2(columnSheet, 6, columnSeq, array[6]);
					writeCell2(columnSheet, 7, columnSeq, array[7]);
					writeCell2(columnSheet, 8, columnSeq, " ");
				}
				
				//����"����"ҳ
				if (type.equalsIgnoreCase("INDEX")) {
					if (count != 4){
						createFlag = false;
						System.out.println("INDEX���ݸ�ʽ����" + temp);
						break;
					}
					
					indexParser.parse(array[3]);
//					System.out.println("index col_cnt:" + indexParser.col_cnt);
					for (int i=0; i<indexParser.col_cnt; i++) {
						if (i == 0) {
							writeCell2(indexSheet, 0, indexSeq, array[1]);
							writeCell2(indexSheet, 1, indexSeq, array[2]);
						} else {
							writeCell2(indexSheet, 0, indexSeq, null);
							writeCell2(indexSheet, 1, indexSeq, null);
						}
						
						if (i>0 && i == indexParser.col_cnt-1) {
//							System.out.println("indexSeqLast:" + indexSeqLast );
//							System.out.println("indexSeq:" + indexSeq );
							mergeColCells(indexSheet, 0, indexSeqLast, indexSeq);
							mergeColCells(indexSheet, 1, indexSeqLast, indexSeq);
						}
						writeCell2(indexSheet, 2, indexSeq, indexParser.col_array[i]);
						if (indexParser.type_array[i].equals("+")) {
							writeCell2(indexSheet, 3, indexSeq, "ASC");
						} else {
							writeCell2(indexSheet, 3, indexSeq, "DESC");
						}
						indexSeq++;
					}
					indexSeqLast = indexSeq;
			
				}
	        }

		} catch (Exception e) {
			createFlag = false;
			System.out.println("Excel����ʧ�ܣ�");
			e.printStackTrace();
		} finally {
			if (rwb != null) {
				rwb.close();
			}
			if (wwb != null) {
				try {
					wwb.write();
					wwb.close();
				} catch (Exception e) {
					System.out.println("�ر����ݱ�ṹExcel�ļ�ʧ�ܣ�");
					e.printStackTrace();
				}
			}
		}
		return createFlag;
	}
	
	/**
	 * ��������е��ֶ���
	 * @author user
	 *
	 */
	public class SplitIndex {
		int col_cnt = 0;
		String[] col_array = new String[20];
		String[] type_array = new String[20];

		public SplitIndex() {
		}
		
		public void parse(String index) {
			col_cnt = 0;

		    for(int i = 0; i < index.length(); i++) {
		    	if (index.charAt(i) == '+' || index.charAt(i) == '-') {
		    		type_array[col_cnt] = "" + index.charAt(i);
		    		col_cnt ++;
		    	}
		    }

		    int pos1 = 1;
		    int pos2;
		    for(int i = 0; i < col_cnt-1; i++) {
		    	pos2 = index.indexOf(type_array[i+1],pos1);
		    	col_array[i] = index.substring(pos1, pos2);
		    	pos1 = pos2 + 1;
		    }
		    col_array[col_cnt-1] = index.substring(pos1);
		}
	}
	
	public static void main(String[] args) {
		String tableName = args[0];
		
		String textFilePath = "text/TABLE_" + tableName + ".txt";
		String patternFilePath = "template/ICS_��ṹ_ģ��_V1.0.xls";
		String excelFilePath = "excel/��ṹ_" + tableName + ".xls";
		
		Table2Excel testJxl = Table2Excel.getInstance();
		boolean flag = testJxl.createTable(tableName, patternFilePath, excelFilePath, textFilePath);
		if (flag) {
			System.out.println("�������ݱ�[ " + tableName + " ]�����ļ��ɹ�.");
		}
	}
}
