/**
 * OracleWrapper.java
 * SqlWrapper子类
 * 
 * @author		zhoubing
 * @date   		Jul 4, 2019
 * @revision	v1.0
 */
package org.jftone.jdbc.oracle;

import java.util.List;

import org.jftone.config.Const;
import org.jftone.exception.DbException;
import org.jftone.jdbc.SqlSort;
import org.jftone.jdbc.SqlWrapper;


public final class OracleWrapper extends SqlWrapper {
	
	/**
	 * 组装分页查询
	 * Oracle版本
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
		boolean hasSort = null != sqlSortList && !sqlSortList.isEmpty();
		int i = 0;
		StringBuilder selectSb = new StringBuilder("");
		StringBuilder whereSb = new StringBuilder("");
		for (String fieldName : selectFields) {
			selectSb.append(i > 0 ? Const.SPLIT_COMMA : "").append(fieldName);
			i++;
		}
		if (null != condFields && !condFields.isEmpty()) {
			i = 0;
			for (String fieldName : condFields) {
				whereSb.append(i > 0 ? " AND " : "").append(fieldName + "=?");
				i++;
			}
		}
		if(hasSort) {
			sb.append("SELECT * FROM(");
			
			//一层嵌套查询语句
			sb.append("SELECT ROWNUM AS num_,JFT.* FROM (");
			
			//二层嵌套查询语句
			sb.append("SELECT ").append(selectSb.toString()).append(" FROM " + tableName);
			if(whereSb.length()>0) {
				sb.append(" WHERE ");
				sb.append(whereSb.toString());
			}
			i = 0;
			sb.append(" ORDER BY ");
			for (SqlSort ss : sqlSortList) {
				sb.append(i > 0 ? "," : "").append(ss.getFieldName() + " " + ss.getSort());
				i++;
			}
			//结束二层嵌套
			
			sb.append(") JFT WHERE ROWNUM <=").append(firstResult+maxResults);
			//结束一层嵌套
			
			sb.append(") WHERE num_>=").append(firstResult);
			
		}else {
			sb.append("SELECT * FROM(");
			//嵌套查询语句
			sb.append("SELECT ROWNUM AS num_,").append(selectSb.toString()).append(" FROM " + tableName);
			sb.append(" WHERE ");
			if(whereSb.length()>0) {
				sb.append(whereSb.toString()).append(" AND ");
			}
			sb.append(" ROWNUM <=").append(firstResult+maxResults);
			//结束嵌套
			sb.append(") WHERE num_>=").append(firstResult);
		}
		return sb.toString();
	}

	@Override
	public String buildSelectSQL(String sqlStatement, long firstResult, int maxResults) throws DbException {
		StringBuilder sb = new StringBuilder("SELECT * FROM (");
		sb.append("SELECT ROWNUM AS num_, JFT.* FROM (").append(sqlStatement).append(") JFT ");
		sb.append(" WHERE ROWNUM <=").append(firstResult+maxResults).append(") WHERE num_>=").append(firstResult);
		return sb.toString();
	}
}
