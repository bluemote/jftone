package org.jftone.jdbc;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public enum JdbcType {
	BYTE("byte"){
		@Override
		public void setJdbcValue(PreparedStatement pstmt, int index, Object value) throws SQLException {
			String result = DataType.getStringWithNumber(value);
			pstmt.setByte(index, Byte.parseByte(result));
		}
		@Override
		public Object getJdbcValue(ResultSet rs, int index) throws SQLException{
			return rs.getByte(index);
		}
		@Override
		public Object getJavaValue(String value) {
			return Byte.valueOf(value);
		}
		@Override
		public String toJavaType() {
			return Byte.class.getSimpleName();
		}
	},
	SHORT("short"){
		@Override
		public void setJdbcValue(PreparedStatement pstmt, int index, Object value) throws SQLException {
			String result = DataType.getStringWithNumber(value);
			pstmt.setShort(index, Short.parseShort(result));
		}
		@Override
		public Object getJdbcValue(ResultSet rs, int index) throws SQLException{
			return rs.getShort(index);
		}
		@Override
		public Object getJavaValue(String value) {
			return Short.valueOf(value);
		}
		@Override
		public String toJavaType() {
			return Short.class.getSimpleName();
		}
	},
	INT("int"){
		@Override
		public void setJdbcValue(PreparedStatement pstmt, int index, Object value) throws SQLException {
			String result = DataType.getStringWithNumber(value);
			pstmt.setInt(index, Integer.parseInt(result));
		}
		@Override
		public Object getJdbcValue(ResultSet rs, int index) throws SQLException{
			return rs.getInt(index);
		}
		@Override
		public Object getJavaValue(String value) {
			return Integer.valueOf(value);
		}
		@Override
		public String toJavaType() {
			return Integer.class.getSimpleName();
		}
	}, 
	LONG("long"){
		@Override
		public void setJdbcValue(PreparedStatement pstmt, int index, Object value) throws SQLException {
			String result = DataType.getStringWithNumber(value);
			pstmt.setLong(index, Long.parseLong(result));
		}
		@Override
		public Object getJdbcValue(ResultSet rs, int index) throws SQLException{
			return rs.getLong(index);
		}
		@Override
		public Object getJavaValue(String value) {
			return Long.valueOf(value);
		}
		@Override
		public String toJavaType() {
			return Long.class.getSimpleName();
		}
	}, 
	FLOAT("float"){
		@Override
		public void setJdbcValue(PreparedStatement pstmt, int index, Object value) throws SQLException {
			String result = DataType.getStringWithNumber(value);
			pstmt.setFloat(index, Float.parseFloat(result));
		}
		@Override
		public Object getJdbcValue(ResultSet rs, int index) throws SQLException{
			return rs.getFloat(index);
		}
		@Override
		public Object getJavaValue(String value) {
			return Float.valueOf(value);
		}
		@Override
		public String toJavaType() {
			return Float.class.getSimpleName();
		}
	}, 
	DOUBLE("double"){
		@Override
		public void setJdbcValue(PreparedStatement pstmt, int index, Object value) throws SQLException {
			String result = DataType.getStringWithNumber(value);
			pstmt.setDouble(index, Double.parseDouble(result));
		}
		@Override
		public Object getJdbcValue(ResultSet rs, int index) throws SQLException{
			return rs.getDouble(index);
		}
		@Override
		public Object getJavaValue(String value) {
			return Double.valueOf(value);
		}
		@Override
		public String toJavaType() {
			return Double.class.getSimpleName();
		}
	}, 
	DECIMAL("decimal"){
		@Override
		public void setJdbcValue(PreparedStatement pstmt, int index, Object value) throws SQLException {
			String result = DataType.getStringWithNumber(value);
			pstmt.setBigDecimal(index, new BigDecimal(result));
		}
		@Override
		public Object getJdbcValue(ResultSet rs, int index) throws SQLException{
			return rs.getBigDecimal(index);
		}
		@Override
		public Object getJavaValue(String value) {
			return new BigDecimal(value);
		}
		@Override
		public String toJavaType() {
			return BigDecimal.class.getSimpleName();
		}
	},
	BOOLEAN("boolean"){
		@Override
		public void setJdbcValue(PreparedStatement pstmt, int index, Object value) throws SQLException {
			String result = DataType.getStringWithBool(value);
			pstmt.setBoolean(index, Boolean.parseBoolean(result));
		}
		@Override
		public Object getJdbcValue(ResultSet rs, int index) throws SQLException{
			return rs.getBoolean(index);
		}
		@Override
		public Object getJavaValue(String value) {
			return Boolean.valueOf(value);
		}
		@Override
		public String toJavaType() {
			return Boolean.class.getSimpleName();
		}
	},
	STRING("string"){
		@Override
		public void setJdbcValue(PreparedStatement pstmt, int index, Object value) throws SQLException {
			String result = "";
			if(null != value){
				result = String.valueOf(value);
			};
			pstmt.setString(index, result);
		}
		@Override
		public Object getJdbcValue(ResultSet rs, int index) throws SQLException{
			return rs.getString(index);
		}
		@Override
		public Object getJavaValue(String value) {
			return value;
		}
		@Override
		public String toJavaType() {
			return "String";
		}
	},
	DATE("date"){
		@Override
		public void setJdbcValue(PreparedStatement pstmt, int index, Object value) throws SQLException {
			pstmt.setDate(index, DataType.getSqlDateDefault(value));
		}
		@Override
		public Object getJdbcValue(ResultSet rs, int index) throws SQLException{
			java.sql.Date date = rs.getDate(index);
			return null!=date? new Date(date.getTime()) : null;
		}
		@Override
		public Object getJavaValue(String value) {
			return new Date(java.sql.Date.valueOf(value).getTime());
		}
		@Override
		public String toJavaType() {
			return Date.class.getSimpleName();
		}
	}, 
	TIME("time"){
		@Override
		public void setJdbcValue(PreparedStatement pstmt, int index, Object value) throws SQLException {
			pstmt.setTime(index, DataType.getSqlTimeDefault(value));
		}
		@Override
		public Object getJdbcValue(ResultSet rs, int index) throws SQLException{
			java.sql.Time time = rs.getTime(index);
			return null != time? new Date(time.getTime()) : null;
		}
		@Override
		public Object getJavaValue(String value) {
			return new Date(java.sql.Time.valueOf(value).getTime());
		}
		@Override
		public String toJavaType() {
			return Date.class.getSimpleName();
		}
	}, 
	DATETIME("datetime"){
		@Override
		public void setJdbcValue(PreparedStatement pstmt, int index, Object value) throws SQLException {
			pstmt.setTimestamp(index, DataType.getSqlTimestampDefault(value));
		}
		@Override
		public Object getJdbcValue(ResultSet rs, int index) throws SQLException{
			java.sql.Timestamp timestamp = rs.getTimestamp(index);
			return null != timestamp? new Date(timestamp.getTime()) : null;
		}
		@Override
		public Object getJavaValue(String value) {
			return new Date(java.sql.Timestamp.valueOf(value).getTime());
		}
		@Override
		public String toJavaType() {
			return Date.class.getSimpleName();
		}
	},
	OBJECT("object"){		//如果无法识别类型，默认走Object
		@Override
		public void setJdbcValue(PreparedStatement pstmt, int index, Object value) throws SQLException {
			pstmt.setObject(index, value);
		}
		@Override
		public Object getJdbcValue(ResultSet rs, int index) throws SQLException{
			return rs.getObject(index);
		}
		@Override
		public Object getJavaValue(String value) {
			return value;
		}
		@Override
		public String toJavaType() {
			return Object.class.getSimpleName();
		}
	};
	
	private final String type;
	//构造方法
	private JdbcType(String type) {
		this.type = type;
	}
	//返回枚举数据类型
	public String value() {
		return this.type;
	}
	
	/**
	 * 设置PreparedStatement对象属性值
	 * @param pstmt
	 * @param index
	 * @param value
	 * @throws SQLException
	 */
	public abstract void setJdbcValue(PreparedStatement pstmt, int index, Object value) throws SQLException;
	
	/**
	 * 根据枚举类型返回不同的JDBC数据对象
	 * @param rs
	 * @param index
	 * @return
	 * @throws SQLException
	 */
	public abstract Object getJdbcValue(ResultSet rs, int index) throws SQLException;
	
	/**
	 * 根据JDBC数据对象转成Java对象
	 * @param jdbcValue
	 * @return
	 * @throws SQLException
	 */
	public abstract Object getJavaValue(String jdbcValue) throws SQLException;
	
	/**
	 * 返回枚举对应的Java对象名称
	 * @return
	 */
	public abstract String toJavaType();
}
