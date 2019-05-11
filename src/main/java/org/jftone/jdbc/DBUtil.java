/**
 * DBUtil.java
 * 
 * @author		zhoubing
 * @date   		Jul 5, 2011
 * @revision	v1.0
 */
package org.jftone.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jftone.exception.DbException;
import org.jftone.jdbc.handler.ResultSetHandler;

import net.sf.json.JSONArray;


public class DBUtil {
	private static Log log = LogFactory.getLog(DBUtil.class);

	private DBUtil() {
		super();
	}
	
	/**
	 * 关闭Statement对象，可能为null
	 * @param stmt
	 */
	private static void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				log.error("Could not close JDBC Statement", e);
			} 
		}
	}

	/**
	 * 关闭ResultSet对象可能为空
	 * @param rs
	 */
	private static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				log.error("Could not close JDBC ResultSet", e);
			} 
		}
	}
	
	/**
	 * 关闭Connection对象
	 * @param con
	 */
	public static void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				log.error("Could not close JDBC Connection", e);
			} 
		}
	}
	
	/**
	 * 执行给定 SQL 语句，该语句可能为 INSERT、UPDATE 或 DELETE 语句，或者不返回任何内容的 SQL 语句（如 SQL DDL 语句）。 
	 * @param sql
	 * @return
	 * @throws DbException 
	 */
	public static boolean execute(Connection conn, String sql) throws DbException{
		boolean result = false;
		Statement stmt = null;
		long start = System.currentTimeMillis();
		try {
			stmt = conn.createStatement();
			result = stmt.execute(sql);
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行 [%s], 耗时 %d ms", sql, System.currentTimeMillis() - start));
			}
		} catch (SQLException e) {
			log.error("Could not execute SQL: "+sql, e);
			throw new DbException("Could not execute SQL: "+sql, e);
		}finally{
			closeStatement(stmt);
		}
		return result;
	}
	
	/**
	 * 批处理执行更新
	 * @param sql
	 * @param paramList
	 * @return
	 * @throws DbException
	 */
	public static boolean executeBatch(Connection conn, String sql, List<Object[]> paramList, JdbcType[] types) throws DbException{
		boolean result = false;
		PreparedStatement ps = null;
		long start = System.currentTimeMillis();
		try {
			ps = conn.prepareStatement(sql);
			if(null != paramList && paramList.size()>0
					&& null !=types && types.length>0){
				for(Object[] params : paramList){
					for(int i=0, length=params.length; i<length ; i++){
						setJdbcType(ps, i+1, params[i], types[i]);
					}
					ps.addBatch();
				}
			}
			ps.executeBatch();
			result = true;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行 [%s], 参数：%s, 耗时 %d ms", sql, JSONArray.fromObject(paramList).toString(), System.currentTimeMillis() - start));
			}
		} catch (SQLException e) {
			log.error("Could not execute batch SQL: "+sql, e);
			throw new DbException("Could not execute batch SQL: "+sql, e);
		}finally{
			closeStatement(ps);
		}
		return result;
	}
	public static boolean executeBatch(Connection conn, String sql, List<Object[]> paramList) throws DbException{
		if(null == paramList || paramList.size() == 0){
			return executeBatch(conn, sql,null,null);
		}
		Object[] params = paramList.get(0);
		JdbcType[] types = new JdbcType[params.length];
		for(int i=0, length=params.length; i<length ; i++){
			types[i] = DataType.getJdbcType(params[i].getClass());
		}
		return executeBatch(conn, sql, paramList, types);
	}

	/**
	 * 执行给定带参数 SQL 语句，该语句可能为 INSERT、UPDATE 或 DELETE 语句。
	 * @param sql
	 * @param paramList
	 * @return
	 * @throws DbException 
	 */
	public static int update(Connection conn, String sql, Object[] params, JdbcType[] types) throws DbException{
		int result = 0;
		PreparedStatement ps = null;
		long start = System.currentTimeMillis();
		try {
			
			ps = conn.prepareStatement(sql);
			if(null != params && params.length>0
					&& null !=types && types.length>0){
				for(int i=0, length=params.length; i<length ; i++){
					setJdbcType(ps, i+1, params[i], types[i]);
				}
			}
			result = ps.executeUpdate();
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行 [%s], 参数：%s, 耗时 %d ms", sql, JSONArray.fromObject(params).toString(), System.currentTimeMillis() - start));
			}
		} catch (SQLException e) {
			log.error("Could not update SQL: "+sql, e);
			throw new DbException("Could not update SQL: "+sql, e);
		}finally{
			closeStatement(ps);
		}
		return result;
	}
	public static int update(Connection conn, String sql, Object[] params) throws DbException{
		if(null == params || params.length == 0){
			return update(conn, sql,null,null);
		}
		JdbcType[] types = new JdbcType[params.length];
		for(int i=0, length=params.length; i<length ; i++){
			types[i] = DataType.getJdbcType(params[i].getClass());
		}
		return update(conn, sql, params, types);
	}
	
	public static Object insert(Connection conn, String sql, boolean returnFlag, Object[] params, JdbcType[] types) throws DbException{
		Object result = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		long start = System.currentTimeMillis();
		try {
			if(returnFlag){
				ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			}else{
				ps = conn.prepareStatement(sql);
			}
			if(null != params && params.length>0
					&& null !=types && types.length>0){
				for(int i=0, length=params.length; i<length ; i++){
					setJdbcType(ps, i+1, params[i], types[i]);
				}
			}
			ps.executeUpdate();
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行 [%s], 参数：%s, 耗时 %d ms", sql, JSONArray.fromObject(params).toString(), System.currentTimeMillis() - start));
			}
			if(returnFlag){
				rs = ps.getGeneratedKeys();
				if(null != rs && rs.next()){
					result = rs.getString(1);
				}
			}else{
				result = true;
			}
		} catch (SQLException e) {
			log.error("Could not updateBack SQL: "+sql, e);
			throw new DbException("Could not updateBack SQL: "+sql, e);
		}finally{
			closeResultSet(rs);
			closeStatement(ps);
		}
		return result;
	}
	/**
	 * 执行给定带参数 SQL 语句，该语句可能为 INSERT、UPDATE 或 DELETE 语句
	 * 返回自动生成键值，比如mysql，SQL Server自动生成主键
	 * @param sql
	 * @param params
	 * @return	自动生成主键
	 * @throws DbException 
	 */
	public static boolean insert(Connection conn, String sql, Object[] params, JdbcType[] types) throws DbException{
		return (Boolean)insert(conn, sql, false, params, types);
	}
	public static boolean insert(Connection conn, String sql, Object... params) throws DbException{
		if(null == params || params.length == 0){
			return insert(conn, sql,null,null);
		}
		JdbcType[] types = new JdbcType[params.length];
		for(int i=0, length=params.length; i<length ; i++){
			types[i] = DataType.getJdbcType(params[i].getClass());
		}
		return (Boolean)insert(conn, sql, false, params, types);
	}
	
	/**
	 * 统计记录个数
	 * @param conn
	 * @param sql
	 * @param handler
	 * @return
	 * @throws DbException
	 */
	public static long count(Connection conn, String sql) throws DbException{
		return count(conn, sql, null, null);
	}
	public static long count(Connection conn, String sql, Object[] params, JdbcType[] types) throws DbException{
		long count = 0;
		ResultSet rs = null;
		PreparedStatement ps = null;
		long start = System.currentTimeMillis();
		try {
			ps = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			if(null != params && params.length>0 
					&& null !=types && types.length>0){
				ps.clearParameters();
				for(int i=0, length=params.length; i<length ; i++){
					setJdbcType(ps, i+1, params[i], types[i]);
				}
			}
			rs = ps.executeQuery();
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行 [%s], 参数：%s, 耗时 %d ms", sql, JSONArray.fromObject(params).toString(), System.currentTimeMillis() - start));
			}
			if(rs.next()){
				count = rs.getLong(1);
			}
		} catch (SQLException e) {
			log.error("Could not query SQL: "+sql, e);
			throw new DbException("Could not query SQL: "+sql, e);
		}finally{
			closeResultSet(rs);
			closeStatement(ps);
		}
		return count;
	}
	public static long count(Connection conn, String sql, Object... params) throws DbException{
		if(null == params || params.length == 0){
			return count(conn, sql, null,null);
		}
		JdbcType[] types = new JdbcType[params.length];
		for(int i=0, length=params.length; i<length ; i++){
			types[i] = DataType.getJdbcType(params[i].getClass());
		}
		return count(conn, sql, params, types);
	}
	
	/**
	 * 一般查询
	 * @param sql
	 * @return 返回ResultSet对象
	 * @throws DbException 
	 */
	public static <T> T query(Connection conn, String sql, ResultSetHandler<T> handler) throws DbException{
		return query(conn, sql, null, null, handler);
	}
	/**
	 * 执行带参数查询
	 * @param sql
	 * @param paramList
	 * @return
	 * @throws DbException 
	 */
	public static <T> T query(Connection conn, String sql, Object[] params, JdbcType[] types, ResultSetHandler<T> handler) throws DbException{
		ResultSet rs = null;
		PreparedStatement ps = null;
		long start = System.currentTimeMillis();
		try {
			ps = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			if(null != params && params.length>0 
					&& null !=types && types.length>0){
				ps.clearParameters();
				for(int i=0, length=params.length; i<length ; i++){
					setJdbcType(ps, i+1, params[i], types[i]);
				}
			}
			rs = ps.executeQuery();
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行 [%s], 参数：%s, 耗时 %d ms", sql, JSONArray.fromObject(params).toString(), System.currentTimeMillis() - start));
			}
			return handler.handle(rs);
		} catch (SQLException e) {
			log.error("Could not query SQL: "+sql, e);
			throw new DbException("Could not query SQL: "+sql, e);
		}finally{
			closeResultSet(rs);
			closeStatement(ps);
		}
	}
	public static <T> T query(Connection conn, String sql, ResultSetHandler<T> handler, Object... params) throws DbException{
		if(null == params || params.length == 0){
			query(conn, sql, handler, null,null);
		}
		JdbcType[] types = new JdbcType[params.length];
		for(int i=0, length=params.length; i<length ; i++){
			types[i] = DataType.getJdbcType(params[i].getClass());
		}
		return query(conn, sql, params, types, handler);
	}
	
	public static boolean existTable(Connection conn, String tableName) throws DbException{
		boolean result = false;
		ResultSet rs = null;
		long start = System.currentTimeMillis();
		try {
			rs = conn.getMetaData().getTables(conn.getCatalog(), null, tableName, new String[]{"TABLE"});
			if(rs.next()){
				result = true;
			}	
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 查询数据库表[%s], 耗时 %d ms", tableName, System.currentTimeMillis() - start));
			}
		} catch (SQLException e) {
			log.error("查询是否存在数据库表: "+tableName, e);
			throw new DbException("查询是否存在数据库表: "+tableName, e);
		}finally{
			closeResultSet(rs);
		}
		return result;
	}
	
	public static boolean call(Connection conn, String sql, Object[] params, JdbcType[] types) throws DbException{
		boolean flag = false;
		CallableStatement ps = null;
		long start = System.currentTimeMillis();
		try {
			ps = conn.prepareCall("{"+sql+"}");
			if(null != params && params.length>0 
					&& null !=types && types.length>0){
				ps.clearParameters();
				for(int i=0, length=params.length; i<length ; i++){
					setJdbcType(ps, i+1, params[i], types[i]);
				}
			}
			flag = ps.execute();
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行 [%s], 参数：%s, 耗时 %d ms", sql, JSONArray.fromObject(params).toString(), System.currentTimeMillis() - start));
			}
		} catch (SQLException e) {
			log.error("Could not query SQL: "+sql, e);
			throw new DbException("Could not query SQL: "+sql, e);
		}finally{
			closeStatement(ps);
		}
		return flag;
	}
	
	/**
	 * 参数转换
	 * @param pstmt
	 * @param index
	 * @param value
	 * @throws DbException 
	 * @throws SQLException
	 */
	private static void setJdbcType(PreparedStatement pstmt, int index, Object value, JdbcType type) throws DbException{
		try{
			type.setJdbcValue(pstmt, index, value);
		}catch(SQLException e){
			log.error("Statement setter JDBC value error", e);
			throw new DbException("Statement setter JDBC value error", e);
		}
	}
	 
}
