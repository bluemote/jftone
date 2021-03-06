package org.jftone.datasource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.jftone.config.Const;
import org.jftone.transaction.TransactionSynchronizationManager;

public final class DataSourceSynchronizationManager {
	/**
	 * 数据源本地连接标志
	 * routeKey => mapKey
	 * routeKey = 集群名或数据库名
	 * mapKey = 对于集群模式，根据事务控制情况，为集群名+主从配置KEY，对于非集群模式，即为数据源KEY
	 * 即如果是非集群模式，则routeKey和mapKey相等
	 */
	private static ThreadLocal<Map<String, String>> datasourceNameLocal = new ThreadLocal<>();
	
	/**
	 * 获取指定数据源标识名
	 * @param dataSourceType
	 * @return
	 */
	public static String getDataSourceName(String routekey) { 
		Map<String, String> map = datasourceNameLocal.get();
		if(null == map || !map.containsKey(routekey)){
			setDataSourceName(routekey);
		}
        return datasourceNameLocal.get().get(routekey);
	}
	public static void clearDataSourceName(String routekey) {
		Map<String, String> map = datasourceNameLocal.get();
		if(null == map){
			return;
		}
		map.remove(routekey);
	}
	
	/**
	 * 获取数据源实际映射对应的KEY
	 * 如果是普通数据源，则为数据源配置名称
	 * 如果是为集群读写分离，则为集群读写配置名称
	 * @param routeKey
	 * @return
	 * @throws SQLException
	 */
	public static String getDataSourceMappingKey(String routeKey, boolean txFlag){
		//如果非集群配置名，则直接返回
		if(!DataSourceContext.existDomain(routeKey)){
			return routeKey;
		}
		return routeKey+Const.SYMBOL_POINT+(txFlag? ClusterDataSource.KEY_MASTER : ClusterDataSource.KEY_SLAVE);
	}
	
	/**
	 * 根据数据源配置路由名获取真实的路由KEY
	 * 此方式主要是在运行时，根据上下文判断
	 * @param routeName
	 * @return
	 */
	public static String getRuntimeDataSourceMappingKey(String routeName){
		String routeKey = null;
		//如果不是集群域前缀KEY，则直接返回数据源路由名
		if(!DataSourceContext.existDomain(routeName)){
			routeKey = routeName;
			return routeKey;
		}
		//如果是集群数据源路由KEY，则判断是否为事务KEY
		routeKey = routeKey+Const.SYMBOL_POINT + ClusterDataSource.KEY_MASTER;
		if(TransactionSynchronizationManager.isTransactionActive(routeKey)){
			return routeKey;
		}
		routeKey = routeKey+Const.SYMBOL_POINT + ClusterDataSource.KEY_SLAVE;
		return routeKey;
	}
	
	/**
	 * 设置指定数据源标识名，明确指定是否需要开启事务
	 * 因为涉及到读写分离等情况，这个时候需要从主库获取连接
	 * @param routeDsName
	 * @param transaction
	 */
	private static void setDataSourceName(String keyName) { 
		setDataSourceName(keyName, null); 
    }
	
	private static void setDataSourceName(String keyName, String dataSourceName) { 
		Map<String, String> map = datasourceNameLocal.get();
    	if(null == map || map.isEmpty()){
    		map = new HashMap<String, String>();
    		datasourceNameLocal.set(map);
    	}
    	if(null == dataSourceName){
    		dataSourceName = DataSourceContext.getRealDataSourceName(keyName);
    	}
    	map.put(keyName, dataSourceName); 
	}
	
	public static void clear(){ 
		datasourceNameLocal.remove();
    }
}
