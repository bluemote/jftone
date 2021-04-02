/**
 * DBScan.java
 * 扫描数据库表映射到数据库内存对象中
 * 这是一个抽象类，后续如果有其他版本的数据映射需要实现的需要继承这个类
 * 
 * zhoubing
 * Jul 7, 2011
 */
package org.jftone.jdbc;

import java.sql.Connection;

import org.jftone.exception.DbException;
import org.jftone.util.IData;

public abstract class DBScan {

	protected Connection conn;
	
	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 扫描书库表并存放到数据库表映射内存
	 * @param conn
	 * @throws DbException
	 */
	public abstract void scanDBTable() throws DbException;
	
	/**
	 * 根据数据库表名称获取该表的所有字段相关信息
	 * @param table
	 * @throws DbException
	 */
	protected abstract IData<String, FieldStructure> scanTableField(String tableName, TableStructure table) throws DbException;
	
}
