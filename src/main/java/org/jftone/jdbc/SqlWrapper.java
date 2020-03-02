/**
 * SqlWrapper.java
 * SQL解析创建
 * 
 * @author		zhoubing
 * @date   		Jul 16, 2011
 * @revision	v1.0
 */
package org.jftone.jdbc;

import java.util.List;

import org.jftone.config.Const;
import org.jftone.exception.DbException;

public abstract class SqlWrapper {

	/**
	 * 组装一般新增SQL语句
	 * 
	 * @param tableName
	 * @param sqlFields
	 * @return
	 * @throws DbException
	 */
	public String buildInsertSQL(String tableName, List<String> insertFields) throws DbException {
		StringBuilder sb = new StringBuilder();
		StringBuilder tmpSb = new StringBuilder();
		String tmpStr;
		int i = 0;
		sb.append("INSERT INTO " + tableName + "(");
		for (String fieldName : insertFields) {
			tmpStr = i > 0 ? Const.SPLIT_COMMA : "";
			sb.append(tmpStr).append(fieldName);
			tmpSb.append(tmpStr).append("?");
			i++;
		}
		sb.append(") ");
		sb.append("VALUES");
		sb.append("(" + tmpSb.toString() + ") ");
		return sb.toString();
	}

	/**
	 * 组装一般更新SQL语句
	 * 
	 * @param tableName 表名
	 * @param sqlFields 更新的字段组
	 * @param sqlConds  更新的条件字段组
	 * @return
	 * @throws DbException
	 */
	public String buildUpdateSQL(String tableName, List<String> updateFields, List<String> condFields)
			throws DbException {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		sb.append("UPDATE " + tableName + " SET ");
		for (String fieldName : updateFields) {
			sb.append(i > 0 ? Const.SPLIT_COMMA : "").append(fieldName + "=?");
			i++;
		}
		if (null != condFields && !condFields.isEmpty()) {
			sb.append(" WHERE ");
			i = 0;
			for (String condField : condFields) {
				sb.append(i > 0 ? " AND " : "").append(condField + "=?");
				i++;
			}
		}
		return sb.toString();
	}

	/**
	 * 组装删除SQL语句
	 * 
	 * @param tableName
	 * @param sqlConds
	 * @return
	 * @throws DbException
	 */
	public String buildDeleteSQL(String tableName, List<String> condFields, String sqlExpression) throws DbException {
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM " + tableName);
		if (null != condFields) {
			int i = 0;
			sb.append(" WHERE ");
			for (String fieldName : condFields) {
				sb.append(i > 0 ? " AND " : "").append(fieldName + "=?");
				i++;
			}
		}
		if (null != sqlExpression) {
			sb.append(sqlExpression);
		}
		return sb.toString();
	}

	public String buildDeleteSQL(String tableName, List<String> condFields) throws DbException {
		return buildDeleteSQL(tableName, condFields, null);
	}

	/**
	 * 组装统计查询记录语句
	 * 
	 * @param tableName
	 * @param sqlExpression
	 * @return
	 * @throws DbException
	 */
	public String buildCountSQL(String tableName, List<String> condFields) throws DbException {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT count(1) as RECORDS FROM " + tableName);
		if (null != condFields && !condFields.isEmpty()) {
			sb.append(" WHERE ");
			int i = 0;
			for (String fieldName : condFields) {
				sb.append(i > 0 ? " AND " : "").append(fieldName + "=?");
				i++;
			}
		}

		return sb.toString();
	}

	/**
	 * 组装一般的查询SQL语句
	 * 
	 * @param tableName
	 * @param selectFields
	 * @param condFields
	 * @param sqlSortList
	 * @return
	 * @throws DbException
	 */
	public String buildSelectSQL(String tableName, List<String> selectFields, List<String> condFields,
			List<SqlSort> sqlSortList) throws DbException {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		sb.append("SELECT ");
		for (String fieldName : selectFields) {
			sb.append(i > 0 ? Const.SPLIT_COMMA : "").append(fieldName);
			i++;
		}
		sb.append(" FROM " + tableName);
		if (null != condFields && !condFields.isEmpty()) {
			sb.append(" WHERE ");
			i = 0;
			for (String fieldName : condFields) {
				sb.append(i > 0 ? " AND " : "").append(fieldName + "=?");
				i++;
			}
		}
		if (null != sqlSortList && !sqlSortList.isEmpty()) {
			sb.append(" ORDER BY ");
			i = 0;
			for (SqlSort ss : sqlSortList) {
				sb.append(i > 0 ? "," : "").append(ss.getFieldName() + " " + ss.getSort());
				i++;
			}
		}
		return sb.toString();
	}

	/**
	 * 按照model，组装分页查询
	 * 
	 * @param tableName
	 * @param selectFields
	 * @param condFields
	 * @param sqlSortList
	 * @param firstResult
	 * @param maxResults
	 * @return
	 * @throws DbException
	 */
	public abstract String buildSelectSQL(String tableName, List<String> selectFields, List<String> condFields,
			List<SqlSort> sqlSortList, long firstResult, int maxResults) throws DbException;
	
	
	/**
	 * 解析SQL语句，组装分页查询
	 * @param sqlStatement
	 * @param firstResult
	 * @param maxResults
	 * @return
	 * @throws DbException
	 */
	public abstract String buildSelectSQL(String sqlStatement, long firstResult, int maxResults) throws DbException;

}
