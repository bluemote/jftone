/**
 * DB2Wrapper.java
 * SqlWrapper子类
 * 
 * @author		zhoubing
 * @date   		Jul 4, 2019
 * @revision	v1.0
 */
package org.jftone.jdbc.db2;

import java.util.List;

import org.jftone.config.Const;
import org.jftone.exception.DbException;
import org.jftone.jdbc.SqlSort;
import org.jftone.jdbc.SqlWrapper;


public final class DB2Wrapper extends SqlWrapper {
	
	/**
	 * 组装分页查询
	 * DB2版本
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
		StringBuilder sb = new StringBuilder();

		int i = 0;
		StringBuilder selectSb = new StringBuilder("");
		for (String fieldName : selectFields) {
			selectSb.append(i > 0 ? Const.SPLIT_COMMA : "").append(fieldName);
			i++;
		}
		sb.append("SELECT * FROM(");
		
		//一层嵌套查询语句
		sb.append("SELECT rownumber() over() as _num,_JFT.* FROM (");
		
		//二层嵌套查询语句
		sb.append("SELECT ").append(selectSb.toString()).append(" FROM " + tableName);

		if (null != condFields && !condFields.isEmpty()) {
			i = 0;
			sb.append(" WHERE ");
			for (String fieldName : condFields) {
				sb.append(i > 0 ? " AND " : "").append(fieldName + "=?");
				i++;
			}
		}
		if (null != sqlSortList && !sqlSortList.isEmpty()) {
			i = 0;
			sb.append(" ORDER BY ");
			for (SqlSort ss : sqlSortList) {
				sb.append(i > 0 ? "," : "").append(ss.getFieldName() + " " + ss.getSort());
				i++;
			}
		}
		//结束二层嵌套
		
		sb.append(") as _JFT");
		//结束一层嵌套
		
		sb.append(") WHERE _num between ").append(firstResult).append(" and ").append(firstResult+maxResults);
		
		return sb.toString();
	}
}
