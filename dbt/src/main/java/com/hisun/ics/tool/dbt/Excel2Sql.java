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
			System.out.println("取单元格(" + col + "," + row + ")失败！");
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
		 * 读取表索引文件，获取
		 */
		TableIndex ti = new TableIndex(getWorkdir() + "/数据表索引.xls");
		// ti.setIndexFile("数据表索引.xls");
		boolean result = ti.loadTableIndex();
		if (result == false) {
			System.out.println("读取表索引文件失败！");
			createFlag = false;
			return createFlag;
		}

		try {
			os = new PrintWriter(new FileWriter(sqlFilePath, true));

			// 打开表结构文件
			rwb = Workbook.getWorkbook(new File(excelFilePath));

			descSheet = rwb.getSheet("表描述");
			columnSheet = rwb.getSheet("表结构");
			indexSheet = rwb.getSheet("索引");
			if (descSheet == null || columnSheet == null || indexSheet == null) {
				System.out.println("数据表定义文件格式有误！");
				createFlag = false;
				return createFlag;
			}

			// 取表描述
			if (descSheet != null) {
				// 取分区表标志
				String isPartition = getCellValue(descSheet, 1, 6);
				if (isPartition != null && isPartition.equals("是")) {
					partSheet = rwb.getSheet("分区表设置");
					if (partSheet == null) {
						System.out.println("数据表定义文件有误！读取不到表格【分区表设置】");
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

			// 写表描述
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			data = "\n-- 自动生成 for " + ti.dbType + ", 时间:"
					+ sdf.format(new Date());
			os.println(data);
			data = "-- 表描述: " + tableDesc;
			os.println(data);
			if (ti.isDBOracle()) {
				data = "DROP TABLE " + tableName + " CASCADE CONSTRAINTS;";
			} else if (ti.isDBDB2()) {
				data = "DROP TABLE " + tableName + ";";
			} else if (ti.isDBMysql()) {
				data = "DROP TABLE IF EXISTS " + tableName + " CASCADE;";
			}
			os.println(data);

			// 建表语句开始
			data = "CREATE TABLE " + tableName + " (";
			os.println(data);

			// 读取表结构定义，从第2行开始
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
				// 数字类型转换
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
					// CLOB类型
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

				// 非空和默认值处理
				if (ti.isDBDB2()) {
					data = data + " " + colNulls;
					if (colDefault != null && colDefault.trim().length() > 0) {
						data = data + " DEFAULT " + colDefault;
					}
				} else {
					// oracle和mysql相同
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

			// 分区表处理
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
					 * 表空间取值说明：优先分区表，其次取表描述，最后取索引文件
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
					// 不是最后一个分区表需要加","
					if (row < totalRows - 1) {
						data = data + ",";
					} else {
						data = data + "\n);\n";
					}
					os.println(data);
				}
			} else {
				/*
				 * 表空间取值说明：优先取表描述，最后取索引文件
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

			// 写备注信息
			if (comments.trim().length() > 0) {
				os.println(comments);
			}

			// 读取索引，从第2行开始
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
					// 第一个索引
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
					// 下一个索引开始
					// 把前面的索引写入SQL脚本
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
						// 分区表
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

					// 重新组织新的索引
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
			// 写入最后一个索引
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
				// 分区表
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
			System.out.println("生成建表脚本失败！");
			e.printStackTrace();
		}
		return createFlag;
	}

	public static void main(String[] args) {

		String tableName = args[0];
		String sqlFilePath = "sql/" + args[1];

		// String sqlFilePath = "sql/cre_" + tableName + ".sql";
		String excelFilePath = "excel/表结构_" + tableName + ".xls";

		Excel2Sql testJxl = Excel2Sql.getInstance();
		boolean flag = testJxl.createSql(tableName, excelFilePath, sqlFilePath);
		if (flag) {
			System.out.println("生成数据表[ " + tableName + " ]SQL脚本成功.");
		}
	}

	public String getWorkdir() {
		return System.getProperty("DBT_WORKDIR");
	}
}
