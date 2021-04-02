/**
 * Dao.java
 * 数据访问
 * 
 * @author		zhoubing
 * @date   		Jul 8, 2011
 * @revision	v1.0
 */
package org.jftone.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jftone.exception.DaoException;
import org.jftone.jdbc.DataType;
import org.jftone.jdbc.JdbcType;
import org.jftone.util.DataMap;
import org.jftone.util.IData;

public final class DaoUtil {
	private static Logger log = LoggerFactory.getLogger(DaoUtil.class);
	
	/**
	 * 设置Data类型数据
	 * @param data
	 * @param rs
	 * @throws DaoException
	 */
	public static IData<String, Object> setRsToData(ResultSet rs) throws DaoException{
		IData<String, Object> data = new DataMap<String, Object>();
		try{
			ResultSetMetaData rsMeta = rs.getMetaData();
			int column = rsMeta.getColumnCount();
			for(int i=1; i<= column; i++){
				String fieldName = rsMeta.getColumnName(i);
				data.put(fieldName.toUpperCase(), rs.getObject(i));
			}
		}catch(Exception e){
			log.debug("ResultSet对象数据映射Data数据错误：" , e);
			throw new DaoException("ResultSet映射Entity错误");
		}
		return data;
	} 
	
	/**
	 * 获取ResultSet中指定列数据
	 * @param rs
	 * @param fieldClazz
	 * @param index
	 * @return
	 * @throws DaoException
	 */
	public static Object getJdbcValue(ResultSet rs, Class<?> fieldClazz, int index) throws DaoException{
		JdbcType jdbcType = DataType.getJdbcType(fieldClazz);
		return getJdbcValue(rs, jdbcType, index);
	}
	
	/**
	 * 获取ResultSet中指定列数据
	 * @param rs
	 * @param jdbcType
	 * @param index
	 * @return
	 * @throws DaoException 
	 * @throws SQLException
	 */
	public static  Object getJdbcValue(ResultSet rs, JdbcType jdbcType, int index) throws DaoException{
		Object value = null;
		try{
			value = jdbcType.getJdbcValue(rs, index);
		}catch(SQLException e){
			log.error("获取Result对象数据错误", e);
			throw new DaoException("获取Result对象数据错误", e);
		}
		return value;
	}
	
}
