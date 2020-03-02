/**
 * SQLiteWrapper.java
 * SqlWrapper子类
 * 
 * @author		zhoubing
 * @date   		Jul 4, 2019
 * @revision	v1.0
 */
package org.jftone.jdbc.sqlite;

import java.util.List;

import org.jftone.exception.DbException;
import org.jftone.jdbc.SqlSort;
import org.jftone.jdbc.SqlWrapper;


public final class SQLiteWrapper extends SqlWrapper {
	
	/**
	 * 组装分页查询
	 * SQLite版本
	 * 组装条件：主键必须是整数型
	 */
	public String buildSelectSQL(String tableName, List<String> selectFields, 
			List<String> condFields, List<SqlSort> sqlSortList, long firstResult, int maxResults) throws DbException{
		StringBuilder sb = new StringBuilder(buildSelectSQL(tableName, selectFields, condFields, sqlSortList));
		sb.append(" LIMIT ").append(maxResults);
		sb.append(" OFFSET ").append(firstResult);
		return sb.toString();
	}

	@Override
	public String buildSelectSQL(String sqlStatement, long firstResult, int maxResults) throws DbException {
		StringBuilder sb = new StringBuilder(sqlStatement);
		sb.append(" LIMIT ").append(maxResults);
		sb.append(" OFFSET ").append(firstResult);
		return sb.toString();
	}
}
