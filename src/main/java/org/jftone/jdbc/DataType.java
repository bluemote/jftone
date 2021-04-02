/**
 * JdbcType.java
 * JDBC数据类型
 * 
 * @author		zhoubing
 * @date   		May 6, 2012
 * @revision	v1.0
 */
package org.jftone.jdbc;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhoubing
 * 
 */
public final class DataType {
	
	private DataType() {
		super();
	}
	
	/**
	 * 根据数据类型转换成JdbcType枚举
	 * @param fieldClazz
	 * @return
	 */
	public static JdbcType getJdbcType(Class<?> fieldClazz){
		JdbcType jdbcType = JdbcType.STRING;
		if(fieldClazz.isAssignableFrom(Byte.class)
				|| fieldClazz.isAssignableFrom(byte.class)){
			jdbcType = JdbcType.INT;
		}else if(fieldClazz.isAssignableFrom(Short.class)
				|| fieldClazz.isAssignableFrom(short.class)){
			jdbcType = JdbcType.SHORT;
		}else if(fieldClazz.isAssignableFrom(Integer.class)
				|| fieldClazz.isAssignableFrom(int.class)){
			jdbcType = JdbcType.INT;
		}else if(fieldClazz.isAssignableFrom(Long.class)
				|| fieldClazz.isAssignableFrom(long.class)){
			jdbcType = JdbcType.LONG;
		}else if(fieldClazz.isAssignableFrom(Character.class)
				|| fieldClazz.isAssignableFrom(char.class)){
			jdbcType = JdbcType.STRING;
		}else if(fieldClazz.isAssignableFrom(Float.class)
				|| fieldClazz.isAssignableFrom(float.class)){
			jdbcType = JdbcType.FLOAT;
		}else if(fieldClazz.isAssignableFrom(Double.class)
				|| fieldClazz.isAssignableFrom(double.class)){
			jdbcType = JdbcType.DOUBLE;
		}else if(fieldClazz.isAssignableFrom(Boolean.class)
				|| fieldClazz.isAssignableFrom(boolean.class)){
			jdbcType = JdbcType.BOOLEAN;
		}else if(fieldClazz.isAssignableFrom(BigDecimal.class)){
			jdbcType = JdbcType.DECIMAL;
		}else if(fieldClazz.isAssignableFrom(Date.class)){
			jdbcType = JdbcType.DATETIME;
		}else if(fieldClazz.isAssignableFrom(String.class)){
			jdbcType = JdbcType.STRING;
		}else  if(fieldClazz.isAssignableFrom(Object.class)){
			jdbcType = JdbcType.OBJECT;
		}
		return jdbcType;
	}
	
	/**
	 * 根据传入的 数据类型 返回对应的 数据类型枚举
	 * @param dataType 字符对应的数据类型
	 * @return
	 */
	public static JdbcType getJdbcType(String dataType){
		JdbcType jdbcType = JdbcType.OBJECT;
		for(JdbcType jt : JdbcType.values()) {
			if(dataType.equals(jt.value())) {
				jdbcType = jt;
				break;
			}
		}
		return jdbcType;
	}
	
	/**
	 * 把布尔类型对象转换成字符串对象
	 * @param value
	 * @return
	 */
	public static String getStringWithBool(Object value){
		String result = null;
		if(null == value){
			result = "false";
		}else{
			result = String.valueOf(value);
		}
		return result;
	}
	
	/**
	 * 把数字类型对象转换成字符串对象
	 * @param value
	 * @return
	 */
	public static String getStringWithNumber(Object value){
		String result = null;
		if(null == value){
			result = "0";
		}else{
			result = String.valueOf(value);
		}
		return result;
	}
	
	/**
	 * 把日期对象转换成JDBC日期类型值
	 * @param value
	 * @return
	 */
	public static java.sql.Date getSqlDateDefault(Object value){
		java.sql.Date result = null;
		if(null != value){
			result = new java.sql.Date(((Date)value).getTime());
		}
		return result;
	}
	
	/**
	 * 把日期对象转成JDBC时间类型值
	 * @param value
	 * @return
	 */
	public static java.sql.Time getSqlTimeDefault(Object value){
		java.sql.Time result = null;
		if(null != value){
			result = new java.sql.Time(((Date)value).getTime());
		}
		return result;
	}
	
	/**
	 * 把日期对象转成JDBC时间戳类型值
	 * @param value
	 * @return
	 */
	public static java.sql.Timestamp getSqlTimestampDefault(Object value){
		java.sql.Timestamp result = null;
		if(null != value){
			result = new java.sql.Timestamp(((Date)value).getTime());
		}
		return result;
	}
}
