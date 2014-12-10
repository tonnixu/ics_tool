package com.hisun.ics.tool.dbt.provider.excel;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import jxl.Sheet;
import jxl.Workbook;

import com.hisun.ics.tool.dbt.entity.ColumnEntity;
import com.hisun.ics.tool.dbt.entity.ConfigInfo;
import com.hisun.ics.tool.dbt.entity.IndexColumnEntity;
import com.hisun.ics.tool.dbt.entity.IndexEntity;
import com.hisun.ics.tool.dbt.entity.PartitionEntity;
import com.hisun.ics.tool.dbt.entity.PartitionItemEntity;
import com.hisun.ics.tool.dbt.entity.SequenceEntity;
import com.hisun.ics.tool.dbt.entity.TableEntity;
import com.hisun.ics.tool.dbt.util.DBTUtil;
import com.hisun.ics.tool.dbt.util.ExcelUtil;

public class ConfigParse {
	public static ConfigInfo parse(String indexFile) throws Exception {
		Workbook wb = null;

		if (indexFile == null) {
			throw new Exception("未指定数据表索引文件名！");
		}
		try {
			ConfigInfo configInfo = new ConfigInfo();
			// 打开模板文件
			wb = Workbook.getWorkbook(new File(indexFile));
			if (wb == null) {
				throw new Exception("打开数据表索引文件失败！" + indexFile);
			}
			Sheet sheet = wb.getSheet("索引");
			if (sheet == null) {
				throw new Exception("读取表格【索引】失败！");
			}

			parseTableIndex(sheet, configInfo);
			sheet = wb.getSheet("序列");
			if (sheet != null) {
				parseSequenceIndex(sheet, configInfo);
			}
			return configInfo;
		} finally {
			ExcelUtil.closeWorkbook(wb);
		}
	}

	public static ConfigInfo parseTableIndex(Sheet sheet, ConfigInfo configInfo)
			throws Exception {
		String appNam = ExcelUtil.getCellValue(sheet, 2, 1);
		if (appNam == null) {
			throw new Exception("读取表格【索引】的应用名(1行，2列)为空！");
		}
		configInfo.setAppNam(appNam);
		configInfo.setDataTBSName(ExcelUtil.getCellValue(sheet, 2, 2));
		configInfo.setIndexTBSName(ExcelUtil.getCellValue(sheet, 2, 3));
		for (int i = 6; i < sheet.getRows(); i++) {
			String valFlg = ExcelUtil.getCellValue(sheet, 3, i);
			if (!StringUtils.equals(valFlg, "Y")) {
				continue;
			}
			String tableName = ExcelUtil.getCellValue(sheet, 0, i);
			if (StringUtils.isBlank(tableName)) {
				break;
			}
			System.out.println("表:[" + tableName + "]");
			if (configInfo.containsTable(tableName)) {
				throw new Exception("表:[" + tableName + "]重复");
			}
			String filePath = ExcelUtil.getCellValue(sheet, 2, i);
			TableEntity table = parseTable(configInfo, DBTUtil.getWorkdir()
					+ "/" + filePath);
			configInfo.addTable(table);
		}
		return configInfo;
	}

	public static ConfigInfo parseSequenceIndex(Sheet sheet,
			ConfigInfo configInfo) throws Exception {
		for (int i = 1; i < sheet.getRows(); i++) {
			String name = ExcelUtil.getCellValue(sheet, 0, i);
			if (StringUtils.isBlank(name)) {
				break;
			}
			System.out.println("序列:[" + name + "]");
			if (configInfo.containsSequence(name)) {
				throw new Exception("序列:[" + name + "]重复");
			}
			SequenceEntity sequence = new SequenceEntity();
			sequence.setName(name);
			;
			sequence.setMinValue(NumberUtils.toInt(ExcelUtil.getCellValue(
					sheet, 1, i)));
			sequence.setMaxValue(NumberUtils.toInt(ExcelUtil.getCellValue(
					sheet, 2, i)));
			sequence.setCycle(ExcelUtil.getCellValue(sheet, 3, i));
			sequence.setDesc(ExcelUtil.getCellValue(sheet, 4, i));
			configInfo.addSequence(sequence);
		}
		return configInfo;
	}

	public static TableEntity parseTable(ConfigInfo configInfo, String fileName)
			throws Exception {
		Workbook wb = null;
		try {
			wb = Workbook.getWorkbook(new File(fileName));
			TableEntity table = new TableEntity();
			Sheet sheet = wb.getSheet("表描述");
			if (sheet == null) {
				throw new Exception("数据表定义文件有误！读取不到表格【表描述】 sheet");
			}
			parseDescSheet(sheet, configInfo, table);

			sheet = wb.getSheet("表结构");
			if (sheet == null) {
				throw new Exception("数据表定义文件有误！读取不到表格【表结构】 sheet");
			}
			parseColumnSheet(sheet, table);

			sheet = wb.getSheet("索引");
			if (sheet == null) {
				throw new Exception("数据表定义文件有误！读取不到表格【索引】 sheet");
			}
			parseIndexSheet(sheet, table);

			sheet = wb.getSheet("分区表设置");
			if (table.isPartition() && sheet == null) {
				throw new Exception("数据表定义文件有误！读取不到表格【分区表设置】 sheet");
			}
			parsePartitionSheet(sheet, table);
			return table;
		} finally {
			ExcelUtil.closeWorkbook(wb);
		}
	}

	public static TableEntity parseDescSheet(Sheet sheet,
			ConfigInfo configInfo, TableEntity table) {
		table.setName(ExcelUtil.getCellValue(sheet, 1, 1));
		table.setPartition(ExcelUtil.getCellValue(sheet, 1, 6));
		table.setDesc(ExcelUtil.getCellValue(sheet, 1, 2));
		table.setDataTBSName(ExcelUtil.getCellValue(sheet, 1, 7));
		if (StringUtils.isBlank(table.getDataTBSName())) {
			table.setDataTBSName(configInfo.getDataTBSName());
		}
		table.setIndexTBSName(ExcelUtil.getCellValue(sheet, 1, 8));
		if (StringUtils.isBlank(table.getIndexTBSName())) {
			table.setIndexTBSName(configInfo.getIndexTBSName());
		}
		return table;
	}

	public static TableEntity parseColumnSheet(Sheet sheet, TableEntity table) {
		int totalRow = sheet.getRows();
		for (int row = 1; row < totalRow; row++) {
			ColumnEntity column = new ColumnEntity();
			column.setName(ExcelUtil.getCellValue(sheet, 0, row));
			if (StringUtils.isBlank(column.getName())) {
				break;
			}
			column.setDesc(ExcelUtil.getCellValue(sheet, 1, row));
			column.setType(ExcelUtil.getCellValue(sheet, 3, row));
			column.setLength(NumberUtils.toInt(ExcelUtil.getCellValue(sheet, 4,
					row)));
			column.setScale(NumberUtils.toInt(ExcelUtil.getCellValue(sheet, 5,
					row)));
			column.setNullable(ExcelUtil.getCellValue(sheet, 6, row));
			column.setDefaultValue(ExcelUtil.getCellValue(sheet, 7, row));
			table.addColumn(column);
		}
		return table;
	}

	public static TableEntity parseIndexSheet(Sheet sheet, TableEntity table)
			throws Exception {
		int totalRow = sheet.getRows();
		IndexEntity index = null;
		for (int row = 1; row < totalRow; row++) {
			String indexName = ExcelUtil.getCellValue(sheet, 0, row);
			if (StringUtils.isNotBlank(indexName)) {
				if (index != null) {
					table.addIndex(index);
				}
				index = new IndexEntity();
				index.setName(ExcelUtil.getCellValue(sheet, 0, row));
				index.setType(ExcelUtil.getCellValue(sheet, 1, row));
			}
			IndexColumnEntity indexColumn = new IndexColumnEntity();
			String name = ExcelUtil.getCellValue(sheet, 2, row);
			if (StringUtils.isBlank(name)) {
				continue;
			}
			ColumnEntity column = table.getColumn(name);
			if (column == null) {
				throw new Exception("表:[" + table.getName() + "], 索引列:[" + name
						+ "]不存在");
			}
			indexColumn.setColumn(column);
			indexColumn.setSortType(ExcelUtil.getCellValue(sheet, 3, row));
			index.addColumn(indexColumn);
		}
		table.addIndex(index);
		return table;
	}

	public static TableEntity parsePartitionSheet(Sheet sheet, TableEntity table) {
		PartitionEntity partition = new PartitionEntity();
		// 分区语句
		String value = ExcelUtil.getCellValue(sheet, 1, 1);
		if (StringUtils.isNotBlank(value)) {
			partition.setStatement(value);
		}

		partition.setKey(ExcelUtil.getCellValue(sheet, 1, 2));
		if (!table.containsColum(partition.getKey())) {
			throw new IllegalArgumentException("表:[" + table.getName()
					+ "] 分区键:[" + partition.getKey() + "]不存在");
		}

		int totalRow = sheet.getRows();
		for (int row = 5; row < totalRow; row++) {
			PartitionItemEntity partitionItem = new PartitionItemEntity();
			partitionItem.setName(ExcelUtil.getCellValue(sheet, 0, row));
			if (StringUtils.isBlank(partitionItem.getName())) {
				continue;
			}
			partitionItem.setStartValue(ExcelUtil.getCellValue(sheet, 1, row));
			partitionItem.setEndValue(ExcelUtil.getCellValue(sheet, 2, row));
			partitionItem.setDataTbsName(ExcelUtil.getCellValue(sheet, 3, row));
			partitionItem
					.setIndexTbsName(ExcelUtil.getCellValue(sheet, 4, row));
			partition.addPartitionItem(partitionItem);
		}
		table.setPartition(partition);
		return table;
	}
}