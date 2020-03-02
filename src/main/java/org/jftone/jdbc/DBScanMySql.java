/**
 * DBScanMySql.java
 * 扫描数据库表映射到数据库内存对象中
 * MySQL版本数据库
 * 
 * zhoubing
 * Jul 5, 2011
 */
package org.jftone.jdbc;

import java.sql.ResultSet;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jftone.exception.DbException;
import org.jftone.jdbc.handler.ResultSetHandler;
import org.jftone.util.DataMap;
import org.jftone.util.IData;
import org.jftone.util.ObjectUtil;

public class DBScanMySql extends DBScan {
	
	private Logger log = LoggerFactory.getLogger(DBScanMySql.class);
	
	/**
	 * 扫描书库表并存放到数据库表映射内存
	 * @param conn
	 * @throws DbException
	 */
	public void scanDBTable() throws DbException{
		
		DBUtil.query(conn, "show tables", new ResultSetHandler<Object>(){
			public Object handle(ResultSet rs) throws DbException{
				try {
					while(rs.next()){
						String tableName = rs.getString(1);
						/**
						 * 如果内存映射关系中已经存在扫描过的表，则跳过当前映射操作
						 */
						if(DBRepository.existTable(ObjectUtil.getClassName(tableName))){
							continue;
						}
						//构建表结构对象
						TableStructure table = new TableStructure(tableName);
						/**
						 * 扫描表对应的字段信息并组装
						 */
						IData<String, FieldStructure> field = scanTableField(tableName, table);
	
						
						table.setFieldData(field);
						DBRepository.addTable(table);	//装在表数据映射到内存中
					}
				}catch (Exception e) {
					log.error("throw error in scanning tables", e);
					throw new DbException(e);
				}
				return null;
			}	
		});
	}
	
	/**
	 * 根据数据库表名称获取该表的所有字段相关信息
	 * @param table
	 * @throws DbException
	 */
	protected IData<String, FieldStructure> scanTableField(String tableName, final TableStructure table) throws DbException {
		final IData<String, FieldStructure> map = new DataMap<String, FieldStructure>();
		/**
		 * 执行以下语句可以获取表字段信息：
		 * 1、show COLUMNS from tableName;
		 * 2、describe tableName;
		 */
		DBUtil.query(conn, "describe "+tableName, new ResultSetHandler<Object>(){
			public Object handle(ResultSet rs) throws DbException{
				try {
					/**
					 * 扫描表字段信息
					 *  +--------+---------------+------+-----+-------------------+-------+
						| Field  | Type          | Null | Key | Default           | Extra |
						+--------+---------------+------+-----+-------------------+-------+
						| field1 | float(9,3)    | NO   | PRI |                   |auto_increment |
						| field2 | double(15,3)  | NO   | MUL |                   |       |
						| field3 | date          | YES  |     | NULL              |       |
						| field4 | datetime      | YES  |     | NULL              |       |
						| field5 | smallint(6)   | YES  |     | NULL              |       |
						| field6 | timestamp     | YES  |     | CURRENT_TIMESTAMP |       |
						| field7 | decimal(11,3) | YES  |     | NULL              |       |
						| field8 | text          | YES  |     | NULL              |       |
						+--------+---------------+------+-----+-------------------+-------+
					 */
					JdbcType type = null;
					while(rs.next()){
						
						String fieldName = rs.getString("Field");
						type = getFieldDataType(rs.getString("Type"));
						String empty = rs.getString("Null");
						String key = rs.getString("Key");
						if(key.equals("PRI")){					
							table.setPrimaryKey(fieldName);
							table.setPrimaryType(type);
							/**
							 * 暂时设置为数据表自动增长主键，后续再来补充完善
							 */
							String extra = rs.getString("Extra");
							if(null != extra && extra.equals("auto_increment")){
								table.setGenerateType(TableStructure.PK_NATIVE);
							}else{
								table.setGenerateType(TableStructure.PK_CUSTOMIZE);
							}
						}
						FieldStructure field = new FieldStructure(fieldName, type); 			//创建字段结构对象
						if(null != empty && "NO".equals(empty)){
							field.setEmpty(false);									//设置字段不允许为空，默认为空
						}
						map.put(ObjectUtil.getPropertyName(fieldName), field);
					}
				} catch (Exception e) {
					log.error("throw error in scanning table's fields", e);
					throw new DbException(e);
				}
				return null;
			}
		});
		return map;
	}
	
	/**
	 * 根据返回的字段类型确定系统设定的对象数据类型
	 * @param fieldType
	 * @return
	 */
	private JdbcType getFieldDataType(String fieldType){
		if(fieldType == null || fieldType.equals("")){
			return JdbcType.STRING;
		}
		JdbcType dataType = null;
		if(fieldType.startsWith("bigint") ){	
			//长整型
			dataType = JdbcType.LONG;
		}else if(fieldType.startsWith("smallint") || fieldType.startsWith("tinyint")){
			//短整型
			dataType = JdbcType.SHORT;
		}else if(fieldType.startsWith("int")){
			//整型
			dataType = JdbcType.INT;
		}else if(fieldType.startsWith("float")){
			//浮点类型
			dataType = JdbcType.FLOAT;
		}else if(fieldType.startsWith("double")){
			//双精度浮点类型
			dataType = JdbcType.DOUBLE;
		}else if(fieldType.equals("time")){
			//时间类型
			dataType = JdbcType.TIME;
		}else if(fieldType.equals("timestamp") 
				|| fieldType.equals("datetime")){
			//时间戳类型
			dataType = JdbcType.DATETIME;
		}else if(fieldType.equals("date")){
			//日期类型
			dataType = JdbcType.DATE;
		}else if(fieldType.indexOf("text")!=-1 || fieldType.indexOf("char")!=-1){
			//字符串类型
			dataType = JdbcType.STRING;
		}else if(fieldType.startsWith("decimal")){
			//字符串类型
			dataType = JdbcType.DECIMAL;
		}else {
			
		}
		return dataType;
	}
}
