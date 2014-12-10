package com.hisun.ics.tool.dbt;

import java.io.FileWriter;
import java.io.PrintWriter;

import com.hisun.ics.tool.dbt.entity.ConfigInfo;

public abstract class DBProvider {
	public abstract void process(PrintWriter fw, ConfigInfo configInfo) throws Exception;
}
