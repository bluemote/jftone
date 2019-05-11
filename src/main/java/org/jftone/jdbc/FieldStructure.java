/**
 * FieldStructure.java
 * 文件主要描述数据库表中某个字段的相关属性
 * 
 * zhoubing
 * Jul 5, 2011
 */
package org.jftone.jdbc;

public class FieldStructure {
	protected String name;								//表字段名称，对应数据库表中的列名，保持一致
	protected JdbcType type = JdbcType.STRING;			//字段类型
	protected boolean empty = true;						//是否允许为空
	
	/**
	 * 创建一般数据表字段数据对象
	 * @param name
	 * @param type
	 */
	public FieldStructure(String name, JdbcType type) {
		this.name = name;
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public JdbcType getType() {
		return type;
	}
	public void setType(JdbcType type) {
		this.type = type;
	}
	public boolean isEmpty() {
		return empty;
	}
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
	
}
