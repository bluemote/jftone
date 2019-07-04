/**
 * MySQLWrapper.java
 * SqlWrapper子类
 * 
 * @author		zhoubing
 * @date   		Jul 27, 2011
 * @revision	v1.0
 */
package org.jftone.jdbc.mysql;

import java.util.List;

import org.jftone.config.Const;
import org.jftone.exception.DbException;
import org.jftone.jdbc.SqlSort;
import org.jftone.jdbc.SqlWrapper;


public final class MySQLWrapper extends SqlWrapper {
	
	/**
	 * 组装分页查询
	 * MySQL版本
	 * 组装条件：主键必须是整数型
	 * @param tableName
	 * @param sqlFields
	 * @param pkFields
	 * @param sqlExpression
	 * @param List<SqlSort> sqlSortList
	 * @param firstResult
	 * @param maxResults
	 * @return
	 * @throws DbException
	 */
	public String buildSelectSQL(String tableName, List<String> selectFields, 
			List<String> condFields, List<SqlSort> sqlSortList, long firstResult, int maxResults) throws DbException{
		StringBuilder sb = new StringBuilder(buildSelectSQL(tableName, selectFields, condFields, sqlSortList));
		sb.append(" LIMIT ").append(firstResult);
		sb.append(Const.SPLIT_COMMA).append(maxResults);
		return sb.toString();
	}
}
