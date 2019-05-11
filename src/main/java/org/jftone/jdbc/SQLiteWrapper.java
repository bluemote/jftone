/**
 * MySQLWrapper.java
 * SqlWrapper子类
 * 
 * @author		zhoubing
 * @date   		Jul 27, 2011
 * @revision	v1.0
 */
package org.jftone.jdbc;

import java.util.List;

import org.jftone.config.Const;
import org.jftone.exception.DbException;


public final class SQLiteWrapper extends SqlWrapper {
	
	/**
	 * 组装分页查询
	 * MySQL版本
	 * 组装条件：主键必须是整数型
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
			List<String> condFields, long firstResult, int maxResults) throws DbException{
		StringBuilder sb = new StringBuilder();
		int i=0;
		sb.append("SELECT ");
		for(String fieldName : selectFields){
			sb.append(i>0? Const.SPLIT_COMMA : "").append(fieldName);
			i++;
		}
		sb.append(" FROM "+tableName);
		if(null != condFields && condFields.size()>0){
			sb.append(" WHERE ");
			i=0;
			for(String fieldName : condFields){
				sb.append(i>0? " AND " : "").append(fieldName+"=?");
				i++;
			}
		}
		sb.append(" LIMIT "+maxResults);
		sb.append(" OFFSET "+firstResult);
		return sb.toString();
	}
}
