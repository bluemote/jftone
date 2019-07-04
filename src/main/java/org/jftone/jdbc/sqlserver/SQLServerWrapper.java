/**
 * SQLServerWrapper.java
 * SqlWrapper子类
 * 
 * @author		zhoubing
 * @date   		Jul 4, 2019
 * @revision	v1.0
 */
package org.jftone.jdbc.sqlserver;

import java.util.List;

import org.jftone.exception.DbException;
import org.jftone.jdbc.SqlSort;
import org.jftone.jdbc.SqlWrapper;


public final class SQLServerWrapper extends SqlWrapper {
	
	/**
	 * 组装分页查询
	 * SQLServer2012及以上版本
	 * @param tableName
	 * @param sqlFields
	 * @param pkFields
	 * @param sqlExpression
	 * @param firstResult
	 * @param maxResults
	 * @return
	 * @throws DbException
	 */
	public String buildSelectSQL(String tableName, List<String> selectFields, 
			List<String> condFields, List<SqlSort> sqlSortList, long firstResult, int maxResults) throws DbException{
		StringBuilder sb = new StringBuilder(buildSelectSQL(tableName, selectFields, condFields, sqlSortList));
		sb.append(" OFFSET ").append(firstResult);
		sb.append(" ROWS FETCH NEXT ").append(maxResults);
		sb.append(" ROWS ONLY");
		return sb.toString();
	}
}
