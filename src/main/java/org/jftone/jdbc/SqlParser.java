/**
 * SqlParser.java
 * SQL解析创建
 * 
 * @author		zhoubing
 * @date   		May 7, 2012
 * @revision	v1.0
 */
package org.jftone.jdbc;
import java.util.ArrayList;
import java.util.List;

import org.jftone.util.StringUtil;

public final class SqlParser {
	public static final String PARAM_PATTERN = "#(\\w+)\\:?(\\w*)#";	//匹配参数中的逻辑
	public static final String PARAM_KEY_HALF = "#";	//报表查询参数封装对象KEY分隔符，包括string和number两种
	public static final String PARAM_KEY_SEPARATOR = ":";	//报表查询参数封装对象KEY分隔符，包括string和number两种
	public static final String COND_PREFIX = "\\<\\!\\-\\s*[\\w\\s\\r\\n\\<\\>\\(\\)\\+\\-\\*\\/\\=\\.\\%\\`\\,]*?";	//报表查询参数sql片段=" <!-	id=$P{key} and -> " 前缀
	public static final String COND_SUFFIX = "[\\w\\s\\r\\n\\<\\>\\(\\)\\+\\-\\*\\/\\=\\.\\%\\`\\,]*?\\s*\\-\\>";	//报表查询参数sql片段=" <!-	id=$P{key} and -> " 后缀
	public static final String COND_PREFIX_SEPARATOR = "<!-";	//报表过滤条件前缀
	public static final String COND_SUFFIX_SEPARATOR = "->";	//报表过滤条件后缀

	public static final String SQL_SENTENCE = "sqlSentence";				//存放SQL语句片段
	public static final String PARAM_VALUES = "paramValues";				//存放SQL参数字段的对应值
	public static final String PARAM_TYPES = "paramTypes";					//存放SQL参数字段的对应值的数据类型
	
	private SqlParser() {
		super();
	}
	/**
	 * 解析sql语句
	 * 例子：<!- where id=#productId:datetime# ->
	 * 
	 * @param paramData
	 */
	public static void assembleSql(SqlStructure sqlStructure) {
		String tmpSQL = sqlStructure.getSql();
		List<String[]> paramList = new ArrayList<String[]>();
		
		List<String> list = StringUtil.getMathchList(tmpSQL, PARAM_PATTERN);
		for (String paramStr : list) {
			String[] params = StringUtil.getMathchGroup(paramStr, PARAM_PATTERN);
			// 记录有效的入参
			paramList.add(params);
		}
		sqlStructure.setParams(paramList);
		if(sqlStructure.getParse()){
			return;
		}
		//重新整理sql语句
		tmpSQL = parseSql(paramList, tmpSQL).trim();
		sqlStructure.setSql(tmpSQL);
	}

	/**
	 * 替换所有有效参数为数据库识别的sql语句
	 * @param params
	 * @param sql
	 * @return
	 */
	private static String parseSql(List<String[]> paramList, String sql) {
		if(null == paramList || paramList.size() == 0){
			return sql;
		}
		//替换参数
		StringBuilder paramBuf = new StringBuilder(""); 
		for(String[] params : paramList){
			paramBuf.delete(0, paramBuf.length());
			paramBuf.append(PARAM_KEY_HALF+params[0]);
			if(params.length>1 && !"".equals(params[1])){
				paramBuf.append(PARAM_KEY_SEPARATOR+params[1]);
			}
			paramBuf.append(PARAM_KEY_HALF);
			sql = sql.replace(paramBuf.toString(), "?");
		}
		return sql;
	}

}
