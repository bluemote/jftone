/**
 * DataSourceContext.java
 * 数据源上下文对象
 * 
 * @author    zhoubing
 * @date      Jul 5, 2011
 * @revision  1.0
 */
package org.jftone.datasource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.jftone.jdbc.MySQLWrapper;
import org.jftone.jdbc.SqlWrapper;

public final class DataSourceContext {
	// 数据库连接池对象
	private static Map<String, DataSource> datasourceMap = new HashMap<>();

	private static Map<String, SqlWrapper> sqlWrapperMap = new HashMap<>();

	private static Map<String, DBType> dbTypeMap = new HashMap<>();

	private static boolean clusterEnabled = false;

	private static String defaultDatasourceName = "jdbc";

	private DataSourceContext() {
		super();
	}

	public static SqlWrapper getSqlWrapper(String routeDsKey) {
		String dataSourceName = DataSourceSynchronizationManager.getDataSourceName(routeDsKey);
		if (null == sqlWrapperMap || !sqlWrapperMap.containsKey(dataSourceName)) {
			SqlWrapper sqlWrapper = null;
			//如果找不到对应的数据库类型，默认采用Mysql
			if (!dbTypeMap.containsKey(dataSourceName)) {
				sqlWrapper = new MySQLWrapper();
			} else {
				sqlWrapper = dbTypeMap.get(dataSourceName).getSqlWrapper();
			}
			sqlWrapperMap.put(dataSourceName, sqlWrapper);
		}
		return sqlWrapperMap.get(dataSourceName);
	}

	/**
	 * 设置数据源数据库对应类型
	 * 
	 * @return
	 */
	static void putDbType(String dsName, DBType dbType) {
		dbTypeMap.put(dsName, dbType);
	}

	/**
	 * 设置数据源
	 * 
	 * @return
	 */
	static void putDatasource(String dsName, DataSource dataSource) {
		datasourceMap.put(dsName, dataSource);
	}

	/**
	 * 设置是否启用集群
	 * 
	 * @return
	 */
	static void enabledCluster() {
		DataSourceContext.clusterEnabled = true;
	}

	/**
	 * 获取集群启用标记
	 * 
	 * @return
	 */
	public static boolean isClusterEnabled() {
		return DataSourceContext.clusterEnabled;
	}
	/**
	 * 
	 * @param clusterDomain
	 * @return
	 */
	public static boolean existDomain(String clusterDomain) {
		return ClusterDataSource.existDomain(clusterDomain);
	}

	/**
	 * 设置缺省数据源名字
	 * 
	 * @param dataSourceName
	 */
	static void setDefaultDataSourceName(String dataSourceName) {
		DataSourceContext.defaultDatasourceName = dataSourceName;
	}

	static String getDefaultDataSourceName() {
		return DataSourceContext.defaultDatasourceName;
	}

	/**
	 * 获取数据库连接
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static DataSource getDataSource() throws SQLException {
		return getDataSource(defaultDatasourceName);
	}

	public static DataSource getDataSource(String dataSourceName) throws SQLException {
		if (!datasourceMap.containsKey(dataSourceName)) {
			throw new SQLException("数据源配置名 [" + dataSourceName + "]对应的数据源不存在");
		}
		return datasourceMap.get(dataSourceName);
	}

	/**
	 * 获取路由数据源标识名 只是一个虚拟的默认路由名字
	 * 如果调用者没有指定是否是集群模式，系统会根据配置的数据自动判断
	 * @param dataSourceName
	 * @return
	 */
	public static String getDefaultRouteName() {
		return getDefaultRouteName(DataSourceContext.clusterEnabled);
	}
	public static String getDefaultRouteName(boolean cluster) {
		return !cluster? DataSourceContext.defaultDatasourceName
				: ClusterDataSource.getDefaultClusterName();
	}

	/**
	 * 获取实际数据源标识名 主要是对缺省命名数据源（为空的数据源）进行转换，并计算对应的集群路由名字
	 * 
	 * @param routeDsName
	 *            经过计算及转换以后的数据源KEY
	 * @return
	 */
	public static String getRealDataSourceName(String routeDsName) {
		String datasourceName = null;
		if (ClusterDataSource.existClusterKeyName(routeDsName)) {
			datasourceName = ClusterDataSource.getRouteDatasourceName(routeDsName);
		} else {
			datasourceName = routeDsName;
		}
		return datasourceName;
	}

	public static void destroyed() throws SQLException {
		if (datasourceMap.isEmpty()){
			return;
		}
		//释放数据库连接
		for(Map.Entry<String, DataSource> ds : datasourceMap.entrySet()){
			if(ds.getValue() instanceof BasicDataSource){
				((BasicDataSource)ds.getValue()).close();
			}
		}
		// 清理集群配置数据
		ClusterDataSource.destroyed();
		datasourceMap.clear();
		dbTypeMap.clear();
		sqlWrapperMap.clear();
		datasourceMap = null;
		dbTypeMap = null;
		sqlWrapperMap = null;
	}
}
