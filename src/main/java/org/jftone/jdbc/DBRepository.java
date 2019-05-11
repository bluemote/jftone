/**
 * DBRepository.java
 * 映射数据表数据
 * 将相关信息保存在内存中
 * 默认信息将根据数据库表结构进行映射
 * 后续可以在映射过程中，生成相对应XML配置文件
 * zhoubing
 * Jul 5, 2011
 */
package org.jftone.jdbc;

import java.util.HashMap;
import java.util.Map;

import org.jftone.exception.CommonException;
import org.jftone.util.ObjectUtil;

public final class DBRepository {
	
	private static Map<String, TableStructure> tableVolume = new HashMap<String, TableStructure>();		//表集合KEY
	private static Map<String, SqlStructure> sqlVolume = new HashMap<String, SqlStructure>();			//SQL集合KEY
	
	private DBRepository() {
		super();
	}
	
	public static void destroyed(){
		tableVolume.clear();
		sqlVolume.clear();
		tableVolume=null;
		sqlVolume=null;
	}
	/**
	 * 返回整个数据库表对象
	 * @return
	 */
	public static Map<String, TableStructure> getTableMart(){
		return tableVolume;
	}
	/**
	 * 获取数据库表大小
	 * @return
	 */
	public static int tableMartSize(){
		return tableVolume.size();
	}
	
	/**
	 * 添加一个TableStructure到tableVolume Map对象
	 * @param table
	 * @throws CommonException 
	 */
	public static void addTable(TableStructure table) throws CommonException{
		String tableName = table.getName();
		tableVolume.put(ObjectUtil.getClassName(tableName), table);
	}
	/**
	 * 获取某个TableStructure对象
	 * @param entityName
	 * @return
	 */
	public static TableStructure getTable(String entityName){
		return tableVolume.get(entityName);
	}
	
	/**
	 * 移除某个表对象
	 * @param entityName
	 */
	public static void delTable(String entityName){
		tableVolume.remove(entityName);
	}
	/**
	 * 判断某个表对象是否存在
	 * @param entityName
	 * @return
	 */
	public static boolean existTable(String entityName){
		return tableVolume.containsKey(entityName);
	}
	
	/**
	 * =======================以下是SQL Statement=============================
	 */
	/**
	 * 返回配置文件中的配置的所有Statement SQL语句
	 * @return
	 */
	public static Map<String, SqlStructure> getSQLMart(){
		return sqlVolume;
	}
	
	/**
	 * 添加SQL Statement到sqlVolume Map对象
	 * @param statementName
	 * @param statement
	 */
	public static void putStatement(String statementName , SqlStructure sqlStructure){
		sqlVolume.put(statementName, sqlStructure);
	}
	
	/**
	 * 获取配置文件中的Statement SQL语句
	 * @param statementName
	 * @return
	 */
	public static SqlStructure getSQLStatement(String statementName){
		SqlStructure sqlStructure = null;
		if(sqlVolume.containsKey(statementName)){
			sqlStructure = sqlVolume.get(statementName);
		}
		return sqlStructure;
	}	
	public static boolean existSQLStatement(String statementName){
		return sqlVolume.containsKey(statementName);
	}
}
