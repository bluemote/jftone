package org.jftone.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jftone.transaction.TransactionSynchronizationManager;

public final class DataSourceUtil {
	private static Log log = LogFactory.getLog(DataSourceUtil.class);

	public static Connection getConnection(String routeDataSourceKey) {
		try {
			// 首先从事务同步管理中获取数据库连接对象
			ConnectionHolder connHolder = TransactionSynchronizationManager
					.getConnectionHolder(routeDataSourceKey);
			if (null != connHolder) {
				//记录当前连接请求次数
				connHolder.requested();
				return connHolder.getConnection();
			}
			String dataSourceName = DataSourceSynchronizationManager.getDataSourceName(routeDataSourceKey);
			Connection conn = DataSourceContext.getDataSource(dataSourceName).getConnection();
			// 如果开启事务管理，则需要存入事务同步管理对象
			if (TransactionSynchronizationManager.isTransactionActive(routeDataSourceKey)) {
				connHolder = new ConnectionHolder(conn);
				connHolder.requested();
				//保存事务上下文数据库连接
				TransactionSynchronizationManager.setConnectionHolder(routeDataSourceKey, connHolder);
			}
			return conn;
		} catch (SQLException e) {
			log.debug("Could not get JDBC Connection", e);
			return null;
		}
	}

	/**
	 * 获取数据库连接
	 * @param routeDataSource
	 * @return
	 */
	public static Connection getConnection(RouteDataSource routeDataSource) {
		String routeDataSourceKey = DataSourceSynchronizationManager
				.getCurrentDataSourceKey(routeDataSource.getRouteDataSource());
		
		return getConnection(routeDataSourceKey);
	}

	/**
	 * 释放数据库连接
	 * @param conn
	 * @param routeDataSource
	 */
	public static void releaseConnection(Connection conn, RouteDataSource routeDataSource) {
		String routeDataSourceKey = DataSourceSynchronizationManager
				.getCurrentDataSourceKey(routeDataSource.getRouteDataSource());
		releaseConnection(conn, routeDataSourceKey);
	}
	
	public static void releaseConnection(Connection conn, String routeDataSourceKey) {
		if (null == conn) {
			return;
		}
		try {
			if (null != routeDataSourceKey) {
				ConnectionHolder connHolder = TransactionSynchronizationManager
						.getConnectionHolder(routeDataSourceKey);
				if (null != connHolder && conn == connHolder.getConnection()) {
					connHolder.released();
					if(connHolder.getReferenceCount() == 0){
						conn.close();
					}
					return;
				}
			}
			conn.close();
		} catch (SQLException e) {
			log.debug("Could not close JDBC Connection", e);
		} catch (Throwable e) {
			log.debug("Unexpected exception on closing JDBC Connection", e);
		}
	}

}
