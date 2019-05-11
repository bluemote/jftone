/**
 * RouteDataSource.java
 * 数据库工厂
 * 
 * @author    zhoubing
 * @date      Dec 28, 2016
 * @revision  1.0
 */
package org.jftone.datasource;

import java.sql.SQLException;

import org.jftone.jdbc.SqlWrapper;



public final class RouteDataSource {
	private String routeDataSource;
	private boolean cluster = false;

	public RouteDataSource(String routeDataSource){
		this.routeDataSource = routeDataSource;
	}
	
	public RouteDataSource(String routeDataSource, boolean cluster){
		this.routeDataSource = routeDataSource;
		this.cluster = cluster;
	}
	public String getRouteDataSource() {
		return routeDataSource;
	}
	
	public boolean isCluster() {
		return cluster;
	}

	public SqlWrapper getSqlWrapper() throws SQLException{
		String dataSourceKey = DataSourceSynchronizationManager.getCurrentDataSourceKey(routeDataSource);
		return DataSourceContext.getSqlWrapper(dataSourceKey);
	}
}
