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
	//仅仅表示配置的数据源KEY，或者集群KEY，获取最终的数据源KEY，集群模式下还需要根据事务控制情况，找到最终集群主从KEY
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
	
	/**
	 * 返回当前路由数据源是否采用集群方式
	 * @return
	 */
	public boolean isCluster() {
		return cluster;
	}
	
	/**
	 * 获取对应数据库SQL解析包装对象
	 * @return
	 * @throws SQLException
	 */
	public SqlWrapper getSqlWrapper() throws SQLException{
		String dataSourceKey = DataSourceSynchronizationManager.getRuntimeDataSourceMappingKey(routeDataSource);
		return DataSourceContext.getSqlWrapper(dataSourceKey);
	}
	
	public DBType getDBType() throws SQLException{
		String dataSourceKey = DataSourceSynchronizationManager.getRuntimeDataSourceMappingKey(routeDataSource);
		return DataSourceContext.getDBType(dataSourceKey);
	}
}
