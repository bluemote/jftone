/**
 * SqlStructure.java
 * 主要描述sql配置的属性
 * 
 * zhoubing
 * May 7, 2012
 */
package org.jftone.jdbc;

import java.util.List;

public class SqlStructure {
	private String name;									//SQL名字，对应配置文件中的id，唯一
	private String sql;										//sql表达式
	private boolean parse;	
	private List<String[]> params;
	
	
	public SqlStructure(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public boolean getParse() {
		return parse;
	}

	public void setParse(boolean parse) {
		this.parse = parse;
	}

	public List<String[]> getParams() {
		return params;
	}

	public void setParams(List<String[]> params) {
		this.params = params;
	}
	
	
}
