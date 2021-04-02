/**
 * TableStructure.java
 * 主要描述数据库表相关属性
 * 
 * zhoubing
 * Jul 5, 2011
 */
package org.jftone.jdbc;

import java.util.Map;

public class TableStructure {
	/**
	 * 数据库表复合主键标识
	 */
	public static final int PK_NATIVE = 1;
	public static final int PK_SEQUENCE = 2;
	public static final int PK_CUSTOMIZE = 3;

	private String name;					//表字段名称，对应数据库中的表名，保持一致
	private String primaryKey;
	private JdbcType primaryType;
	private int generateType ;			//主键生成类型:1=自动生成；2=序列获取；3=自定义(1=native；2=sequnce，3=customize)
	private String identity;			//主键标识,比如为sequence或其他待定
	private Map<String, FieldStructure> fieldData;	//存放表字段

	public TableStructure(String name) {
		this.name = name;
	}

	public Map<String, FieldStructure> getFieldData() {
		return fieldData;
	}

	public void setFieldData(Map<String, FieldStructure> fieldData) {
		this.fieldData = fieldData;
	}
	
	public void addField(String key, FieldStructure field) {
		this.fieldData.put(key, field);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public JdbcType getPrimaryType() {
		return primaryType;
	}

	public void setPrimaryType(JdbcType primaryType) {
		this.primaryType = primaryType;
	}

	public int getGenerateType() {
		return generateType;
	}

	public void setGenerateType(int generateType) {
		this.generateType = generateType;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}
}
