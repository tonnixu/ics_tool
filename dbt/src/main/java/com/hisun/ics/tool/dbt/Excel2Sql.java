package com.hisun.ics.tool.dbt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class Excel2Sql {

	private final static Excel2Sql obj = new Excel2Sql();

	public static Excel2Sql getInstance() {
		return obj;
	}

	public Excel2Sql() {
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

	public boolean createSql(String tableName, String excelFilePath,
			String sqlFilePath) {
		boolean createFlag = true;
		Workbook rwb = null;
		Sheet descSheet = null;
		Sheet columnSheet = null;
		Sheet indexSheet = null;
		PrintWriter os = null;
		String tableDesc = null;
		Sheet partSheet = null;
		String data = null;
		String comments = "";
		String tableDataTbsName = null;
		String tableIndexTbsName = null;

		/*
		 * ��ȡ�������ļ�����ȡ
		 */
		TableIndex ti = new TableIndex(getWorkdir() + "/���ݱ�����.xls");
		// ti.setIndexFile("���ݱ�����.xls");
		boolean result = ti.loadTableIndex();
		if (result == false) {
			System.out.println("��ȡ�������ļ�ʧ�ܣ�");
			createFlag = false;
			return createFlag;
		}

		try {
			os = new PrintWriter(new FileWriter(sqlFilePath, true));

			// �򿪱�ṹ�ļ�
			rwb = Workbook.getWorkbook(new File(excelFilePath));

			descSheet = rwb.getSheet("������");
			columnSheet = rwb.getSheet("��ṹ");
			indexSheet = rwb.getSheet("����");
			if (descSheet == null || columnSheet == null || indexSheet == null) {
				System.out.println("���ݱ����ļ���ʽ����");
				createFlag = false;
				return createFlag;
			}

			// ȡ������
			if (descSheet != null) {
				// ȡ�������־
				String isPartition = getCellValue(descSheet, 1, 6);
				if (isPartition != null && isPartition.equals("��")) {
					partSheet = rwb.getSheet("����������");
					if (partSheet == null) {
						System.out.println("���ݱ����ļ����󣡶�ȡ������񡾷��������á�");
						createFlag = false;
						return createFlag;
					}
				}

				tableDesc = getCellValue(descSheet, 1, 2);
				if (tableDesc != null && tableDesc.trim().length() > 0) {
					comments = comments + "COMMENT ON TABLE " + tableName
							+ " IS '" + tableDesc + "';\n";
				}
				tableDataTbsName = getCellValue(descSheet, 1, 7);
				tableIndexTbsName = getCellValue(descSheet, 1, 8);
			}

			// д������
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			data = "\n-- �Զ����� for " + ti.dbType + ", ʱ��:"
					+ sdf.format(new Date());
			os.println(data);
			data = "-- ������: " + tableDesc;
			os.println(data);
			if (ti.isDBOracle()) {
				data = "DROP TABLE " + tableName + " CASCADE CONSTRAINTS;";
			} else if (ti.isDBDB2()) {
				data = "DROP TABLE " + tableName + ";";
			} else if (ti.isDBMysql()) {
				data = "DROP TABLE IF EXISTS " + tableName + " CASCADE;";
			}
			os.println(data);

			// ������俪ʼ
			data = "CREATE TABLE " + tableName + " (";
			os.println(data);

			// ��ȡ��ṹ���壬�ӵ�2�п�ʼ
			int totalRows = columnSheet.getRows();
			for (int row = 1; row < totalRows; row++) {
				String colName = getCellValue(columnSheet, 0, row);
				String colDesc = getCellValue(columnSheet, 1, row);
				String colType = getCellValue(columnSheet, 3, row);
				String colLength = getCellValue(columnSheet, 4, row);
				String colScale = getCellValue(columnSheet, 5, row);
				String colNulls = getCellValue(columnSheet, 6, row);
				String colDefault = getCellValue(columnSheet, 7, row);

				String lengthStr = null;
				// ��������ת��
				if (colType.equalsIgnoreCase("NUMBER")) {
					if (ti.isDBOracle()) {
						if (NumberUtils.toInt(colScale) != 0) {
							colType = " NUMBER(" + colLength + "," + colScale
									+ ") ";
						} else {
							colType = " NUMBER( " + colLength + ") ";
						}
					} else if (ti.isDBDB2()) {
						if (NumberUtils.toInt(colScale) != 0) {
							colType = " DECIMAL(" + colLength + "," + colScale
									+ ") ";
						} else {
							colType = " BIGINT ";
						}
					} else if (ti.isDBMysql()) {
						if (NumberUtils.toInt(colScale) != 0) {
							colType = " DECIMAL(" + colLength + "," + colScale
									+ ") ";
						} else {
							colType = " BIGINT ";
						}
					}
				} else if (colType.equalsIgnoreCase("CLOB")) {
					// CLOB����
					if (ti.isDBOracle()) {
						colType = " CLOB ";
					} else if (ti.isDBDB2()) {
						if (colLength != null) {
							colType = " CLOB(" + colLength + ") ";
						} else {
							colType = " CLOB ";
						}
					} else if (ti.isDBMysql()) {
						colType = " LONGTEXT ";
					}
				} else {
					colType = colType + "(" + colLength + ")";
				}

				data = "  " + colName + " " + colType;

				// �ǿպ�Ĭ��ֵ����
				if (ti.isDBDB2()) {
					data = data + " " + colNulls;
					if (colDefault != null && colDefault.trim().length() > 0) {
						data = data + " DEFAULT " + colDefault;
					}
				} else {
					// oracle��mysql��ͬ
					if (colDefault != null && colDefault.trim().length() > 0) {
						data = data + " DEFAULT " + colDefault;
					}
					data = data + " " + colNulls;
				}

				if (row < totalRows - 1) {
					data = data + ",";
				}
				data = data;
				os.println(data);

				if (colDesc != null && colDesc.trim().length() > 0) {
					comments = comments + "COMMENT ON COLUMN " + tableName
							+ "." + colName + " IS '" + colDesc + "';\n";
				}
			}

			// ��������
			if (partSheet != null) {
				os.println(")");
				String partKey = getCellValue(partSheet, 1, 1);
				data = "PARTITION BY RANGE (" + partKey + ") (";
				os.println(data);

				totalRows = partSheet.getRows();
				for (int row = 4; row < totalRows; row++) {
					String partName = getCellValue(partSheet, 0, row);
					String startValue = getCellValue(partSheet, 1, row);
					String endValue = getCellValue(partSheet, 2, row);
					String dataTbsName = getCellValue(partSheet, 3, row);
					/*
					 * ��ռ�ȡֵ˵�������ȷ��������ȡ�����������ȡ�����ļ�
					 */
					if (dataTbsName == null || dataTbsName.trim().length() == 0) {
						dataTbsName = tableDataTbsName;
					}
					if (dataTbsName == null || dataTbsName.trim().length() == 0) {
						dataTbsName = ti.dataTbsName;
					}

					data = "";
					if (ti.isDBOracle()) {
						data = "  PARTITION " + partName
								+ " VALUES LESS THAN (";
						if (endValue.equals("MAXVALUE")) {
							data = data + endValue + ")";
						} else {
							data = data + "'" + endValue.trim() + "')";
						}
						if (dataTbsName != null && dataTbsName.length() > 0) {
							data = data + " TABLESPACE " + dataTbsName;
						}
					} else if (ti.isDBDB2()) {
						data = "  PART " + partName;
						if (startValue.equals("MINVALUE")) {
							data = data + " STARTING (MINVALUE)";
						} else if (endValue.equals("MAXVALUE")) {
							data = data + " ENDING (MAXVALUE)";
						} else {
							data = data + " STARTING ('" + startValue
									+ "') ENDING('" + endValue.trim() + "')";
						}
						if (dataTbsName != null && dataTbsName.length() > 0) {
							data = data + " IN \"" + dataTbsName + "\"";
						}
					}
					// �������һ����������Ҫ��","
					if (row < totalRows - 1) {
						data = data + ",";
					} else {
						data = data + "\n);\n";
					}
					os.println(data);
				}
			} else {
				/*
				 * ��ռ�ȡֵ˵��������ȡ�����������ȡ�����ļ�
				 */
				String dataTbsName = tableDataTbsName;
				String indexTbsName = tableIndexTbsName;
				if (dataTbsName == null || dataTbsName.trim().length() == 0) {
					dataTbsName = ti.dataTbsName;
				}
				if (indexTbsName == null || indexTbsName.trim().length() == 0) {
					indexTbsName = ti.indexTbsName;
				}
				if (dataTbsName != null && dataTbsName.trim().length() > 0) {
					if (ti.isDBOracle()) {
						data = ") TABLESPACE " + dataTbsName + ";\n";
					} else if (ti.isDBDB2()) {
						data = ") IN " + dataTbsName;
						if (indexTbsName != null
								&& indexTbsName.trim().length() > 0) {
							data = data + " INDEX IN " + indexTbsName + ";\n";
						}
					} else if (ti.isDBMysql()) {
						data = ") ;\n";
					}
				} else {
					data = ");\n";
				}
				os.println(data);
			}

			// д��ע��Ϣ
			if (comments.trim().length() > 0) {
				os.println(comments);
			}

			// ��ȡ�������ӵ�2�п�ʼ
			int lastRow = 1;
			String lastIndexName = null;

			String indexTbsName = tableIndexTbsName;

			if (indexTbsName == null || indexTbsName.trim().length() == 0) {
				indexTbsName = ti.indexTbsName;
			}

			String lastIndexType = null;
			for (int row = 1; row < indexSheet.getRows(); row++) {
				String indexName = getCellValue(indexSheet, 0, row);
				if (indexName != null) {
					indexName = indexName.trim();
				}
				String indexType = getCellValue(indexSheet, 1, row);
				if (indexType != null) {
					indexType = indexType.trim();
				}
				if (indexType == null || indexType.length() == 0) {
					indexType = lastIndexType;
				} else {
					lastIndexType = indexType;
				}

				// System.out.println("xxxxxx indexType[" + indexType.trim()+
				// "]");
				String colName = getCellValue(indexSheet, 2, row);
				if (colName != null) {
					colName = colName.trim();
				}
				if( StringUtils.isBlank(colName)) {
					continue;
				}
				String sortType = getCellValue(indexSheet, 3, row);
				if (sortType != null) {
					sortType = sortType.trim();
				}

				if (lastIndexName == null) {
					// ��һ������
					// if (ti.dbType.equals(DBTYPE_ORACLE) &&
					// indexType.equalsIgnoreCase("primary key")) {
					// System.out.println("xxxxxx indexType[" + indexType +
					// "]");
					if (indexType.equalsIgnoreCase("primary key")) {
						data = "ALTER TABLE " + tableName + " ADD CONSTRAINT "
								+ indexName + " PRIMARY KEY ";
						data = data + " (" + colName;
					} else {
						data = "CREATE " + indexType.toUpperCase() + " "
								+ indexName + " ON " + tableName;
						data = data + " (" + colName + " " + sortType;
					}
					lastIndexName = indexName;
				} else if (indexName.trim().length() > 0) {
					// ��һ��������ʼ
					// ��ǰ�������д��SQL�ű�
					if (partSheet == null) {
						if (indexTbsName != null
								&& indexTbsName.trim().length() > 0) {
							if (ti.isDBOracle()) {
								data = data + ") TABLESPACE " + indexTbsName
										+ ";";
							} else if (ti.isDBDB2()) {
								data = data + ");";
							} else if (ti.isDBMysql()) {
								data = data + ");";
							}
						} else {
							data = data + ");";
						}
						os.println(data);
					} else {
						// ������
						if (ti.isDBOracle()) {
							data = data + ")\nlocal\n(\n";
							int partTotalRows = partSheet.getRows();
							for (int partRow = 4; partRow < partTotalRows; partRow++) {
								String partName = getCellValue(partSheet, 0,
										partRow);
								data = data + "  PARTITION " + partName;
								if (partRow < partTotalRows - 1) {
									data = data + " ,\n";
								} else {
									data = data + " \n";
								}
							}
							if (indexTbsName != null
									&& indexTbsName.trim().length() > 0) {
								data = data + ") TABLESPACE " + indexTbsName
										+ ";";
							} else {
								data = data + ");";
							}
						} else if (ti.isDBDB2()) {
							data = data + ");";
						}  else if (ti.isDBMysql()) {
							data = data + ");";
						}
						os.println(data);
					}

					// ������֯�µ�����
					// if (ti.dbType.equals(DBTYPE_ORACLE) &&
					// indexType.equalsIgnoreCase("primary key")) {
					// System.out.println("xxxxxx indexType[" + indexType +
					// "]");
					if (indexType.equalsIgnoreCase("primary key")) {
						data = "ALTER TABLE " + tableName + " ADD CONSTRAINT "
								+ indexName + " PRIMARY KEY ";
						data = data + " (" + colName;
					} else {
						data = "CREATE " + indexType.toUpperCase() + " "
								+ indexName + " ON " + tableName;
						data = data + " (" + colName + " " + sortType;
					}
					lastIndexName = indexName;
				} else {
					// System.out.println("xxxxxx indexType[" + indexType +
					// "]");
					if (indexType.equalsIgnoreCase("primary key")) {
						data = data + ", " + colName;
					} else {
						data = data + ", " + colName + " " + sortType;
					}
				}
			}
			// д�����һ������
			if (partSheet == null) {
				if (indexTbsName != null && indexTbsName.trim().length() > 0) {
					if (ti.isDBOracle()) {
						data = data + ") TABLESPACE " + indexTbsName + ";\n";
					} else if (ti.isDBDB2()) {
						data = data + ");\n";
					} else if (ti.isDBMysql()) {
						data = data + ");\n";
					}
				} else {
					data = data + ");\n";
				}
			} else {
				// ������
				if (ti.isDBOracle()) {
					data = data + ")\nlocal\n(\n";
					int partTotalRows = partSheet.getRows();
					for (int partRow = 4; partRow < partTotalRows; partRow++) {
						String partName = getCellValue(partSheet, 0, partRow);
						data = data + "  PARTITION " + partName;
						if (partRow < partTotalRows - 1) {
							data = data + " ,\n";
						} else {
							data = data + " \n";
						}
					}
					if (indexTbsName != null
							&& indexTbsName.trim().length() > 0) {
						data = data + ") TABLESPACE " + indexTbsName + ";\n\n";
					} else {
						data = data + ");\n";
					}
				} else if (ti.isDBDB2()) {
					data = data + ");\n";
				} else if (ti.isDBMysql()) {
					data = data + ");\n";
				}
			}
			os.println(data);
			os.close();
		} catch (Exception e) {
			createFlag = false;
			System.out.println("���ɽ���ű�ʧ�ܣ�");
			e.printStackTrace();
		}
		return createFlag;
	}

	public static void main(String[] args) {

		String tableName = args[0];
		String sqlFilePath = "sql/" + args[1];

		// String sqlFilePath = "sql/cre_" + tableName + ".sql";
		String excelFilePath = "excel/��ṹ_" + tableName + ".xls";

		Excel2Sql testJxl = Excel2Sql.getInstance();
		boolean flag = testJxl.createSql(tableName, excelFilePath, sqlFilePath);
		if (flag) {
			System.out.println("�������ݱ�[ " + tableName + " ]SQL�ű��ɹ�.");
		}
	}

	public String getWorkdir() {
		return System.getProperty("DBT_WORKDIR");
	}
}
