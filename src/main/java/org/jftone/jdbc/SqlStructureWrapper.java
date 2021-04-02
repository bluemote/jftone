/**
 * SqlStructureWrapper.java
 * SQL解析创建
 * 
 * @author		zhoubing
 * @date   		May 7, 2012
 * @revision	v1.0
 */
package org.jftone.jdbc;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jftone.exception.DbException;
import org.jftone.util.StringUtil;

public class SqlStructureWrapper {
	private Logger log = LoggerFactory.getLogger(SqlStructureWrapper.class);

	private SqlStructure sqlStructure;
	
	private String sqlSentence;						//SQL语句片段
	private List<Object> paramValues;				//SQL参数字段的对应值
	private List<JdbcType> paramTypes;			//SQL参数字段的对应值的数据类型

	private SqlStructureWrapper(SqlStructure sqlStructure) {
		this.sqlStructure = sqlStructure;
	}
	
	public static SqlStructureWrapper getWrapper(SqlStructure sqlStructure){
		return new SqlStructureWrapper(sqlStructure);
	}

	public String getSqlSentence() {
		return sqlSentence;
	}
	public void setSqlSentence(String sqlSentence) {
		this.sqlSentence = sqlSentence;
	}
	public List<Object> getParamValues() {
		return paramValues;
	}
	public void setParamValues(List<Object> paramValues) {
		this.paramValues = paramValues;
	}
	public List<JdbcType> getParamTypes() {
		return paramTypes;
	}
	public void setParamTypes(List<JdbcType> paramTypes) {
		this.paramTypes = paramTypes;
	}



	/**
	 * 解析sql语句
	 * 例子：<!- where id=#productId:datetime# ->
	 * 
	 * @param paramData
	 * @throws DbException
	 */
	public void parseParam(Map<String, Object> paramData)
			throws DbException {
		String tmpSQL = sqlStructure.getSql();
		List<String[]> paramList = sqlStructure.getParams();
		boolean parse = sqlStructure.getParse();
		List<Object> paramOjects = new ArrayList<Object>();
		List<JdbcType> paramTypes = new ArrayList<JdbcType>();
		
		boolean hasParam = (null == paramData || paramData.isEmpty())? false : true;
		//如果有参数，则需要解析入参类型和入参对象
		List<String[]> paramNames = new ArrayList<String[]>();
		for (String[] params : paramList) {
			if(parse){
				// 如果没有传入参数，则进行sql无参检查处理
				if(!hasParam || !paramData.containsKey(params[0])){
					tmpSQL = cutEmptyParam(tmpSQL, params);
					continue;
				}
			}
			// 记录有效的入参
			paramNames.add(params);
			JdbcType jdbcType = null;
			if(params.length>1 && !"".equals(params[1])){
				jdbcType = DataType.getJdbcType(params[1]);		//参数中传入的
			}else{
				Class<?> paraTypeClazz = paramData.get(params[0]).getClass();
				jdbcType = DataType.getJdbcType(paraTypeClazz);
			}
			paramTypes.add(jdbcType);
			paramOjects.add(paramData.get(params[0]));
		}
		if(parse){
			//重新整理sql语句
			tmpSQL = this.rebuidSql(paramNames, tmpSQL).trim();
		}
		
		setSqlSentence(tmpSQL);
		setParamValues(paramOjects);
		setParamTypes(paramTypes);
	}

	/**
	 * 替换掉为空的入参sql段，重建sql语句
	 * 
	 * @param sql
	 * @param paramStr
	 * @param flag
	 * @return
	 * @throws DbException 
	 */
	private String cutEmptyParam(String sql, String[] params) throws DbException {
		String tmpSQL = null;
		StringBuilder paramBuf = new StringBuilder();
		paramBuf.append(SqlParser.PARAM_KEY_HALF+params[0]);
		if(params.length>1 && !"".equals(params[1])){
			paramBuf.append(SqlParser.PARAM_KEY_SEPARATOR+params[1]);
		}
		paramBuf.append(SqlParser.PARAM_KEY_HALF);
		List<String> list = StringUtil.getMathchList(sql, SqlParser.COND_PREFIX
				+ paramBuf.toString() + SqlParser.COND_SUFFIX);
		//截掉发现的第一段空参数串
		if (list != null && !list.isEmpty()) {
			tmpSQL = sql.replace(list.get(0), "");	//重设SQL		
		}else{
			log.debug("执行表达式SQL："+ sqlStructure.getName() + "的sql， 解析参数[" + params[0]+ "]错误");
			throw new DbException("执行表达式SQL："+ sqlStructure.getName() + "的sql， 解析参数[" + params[0]+ "]错误");
		}
		return tmpSQL;
	}

	/**
	 * 替换所有有效参数为数据库识别的sql语句
	 * @param params
	 * @param sql
	 * @return
	 */
	private String rebuidSql(List<String[]> paramList, String sql) {
		if(null == paramList || paramList.size() == 0){
			return sql;
		}
		//替换参数
		//替换参数
		StringBuilder paramBuf = new StringBuilder(""); 
		for(String[] params : paramList){
			paramBuf.delete(0, paramBuf.length());
			paramBuf.append(SqlParser.PARAM_KEY_HALF+params[0]);
			if(params.length>1 && !"".equals(params[1])){
				paramBuf.append(SqlParser.PARAM_KEY_SEPARATOR+params[1]);
			}
			paramBuf.append(SqlParser.PARAM_KEY_HALF);
			sql = sql.replace(paramBuf.toString(), "?");
		}
		
		//替换sql中所有查询参数条件识别符<!- 和 ->
		sql = sql.replace(SqlParser.COND_PREFIX_SEPARATOR, "");
		sql = sql.replace(SqlParser.COND_SUFFIX_SEPARATOR, "");
		return sql;
	}

}
