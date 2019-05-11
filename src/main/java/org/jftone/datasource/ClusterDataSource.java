package org.jftone.datasource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

final class ClusterDataSource {
	
	public static final String KEY_MASTER = "master";
	public static final String KEY_SLAVE = "slave";
	
	private static String defaultClusterName = "cluster";
	// 数据库连接池对象
	private static Map<String, String[]> dbClusterMap = new HashMap<>();
	
	private static Map<String, int[]> dbWeightMap = new HashMap<>();
	
	private static Map<String, IDataSourceElect> clusterElectMap = new HashMap<>();
	
	private static HashSet<String> clusterDomainSet = new HashSet<>();
	
	private ClusterDataSource() {
		super();
	}
	
	static String getDefaultClusterName() {
		return defaultClusterName;
	}
	
	/**
	 * 设置缺省集群标记名
	 * @param clusterName
	 */
	static void setDefaultClusterName(String clusterName) {
		ClusterDataSource.defaultClusterName = clusterName;
	}
	
	static void addClusterDomain(String clusterDomain) {
		clusterDomainSet.add(clusterDomain);
	}
	
	/**
	 * 判断集群配置名是否存在
	 * @param clusterDomain
	 * @return
	 */
	static boolean existDomain(String clusterDomain) {
		return clusterDomainSet.contains(clusterDomain);
	}

	/**
	 * 判断是否存在集群主从KEY
	 * @param clusterKeyName
	 * @return
	 */
	static boolean existClusterKeyName(String clusterKeyName) {
		return dbClusterMap.containsKey(clusterKeyName);
	}

	/**
	 * 设置集群数据源
	 * @return
	 */
	static void putDbCluster(String clusterKey, String[] clusters){
		dbClusterMap.put(clusterKey, clusters);
	}
	
	static void putDbWeight(String clusterKey, int[] weights){
		dbWeightMap.put(clusterKey, weights);
	}

	/**
	 * 选择实际路由的数据源标识
	 * @param routeDsName
	 * @return
	 */
	static String getRouteDatasourceName(String clusterKey) {
		String datasourceName = null;
		String[] dsNames = dbClusterMap.get(clusterKey);
		if(dsNames.length == 1){
			datasourceName = dsNames[0];
		}else{
			datasourceName = selectDatasourceName(clusterKey, dsNames);
		}
		return datasourceName;
	}
	
	/**
	 * 选择对应数据源标识名
	 * @param key
	 * @param dsNames
	 * @return
	 */
	private static String selectDatasourceName(String key, String[] dsNames) {
		if(!clusterElectMap.containsKey(key)){
			if(dbWeightMap.containsKey(key)){
				clusterElectMap.put(key, new MutipleDataSourceWeight(dsNames.length, dbWeightMap.get(key)));
			}else{
				//默认为轮询选择数据源标识
				clusterElectMap.put(key, new MutipleDataSourcePoll(dsNames.length));
			}
		}
		return dsNames[clusterElectMap.get(key).getIndex()];
	}
	
	static void destroyed(){
		if(dbClusterMap.isEmpty()) return;
		dbClusterMap.clear();
		clusterElectMap.clear();
		dbWeightMap.clear();
		dbClusterMap = null;
		clusterElectMap = null;
		dbWeightMap = null;
	}
	 
}
