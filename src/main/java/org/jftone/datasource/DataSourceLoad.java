/**
 * GenericDataSource.java
 * 数据库工厂
 * 
 * @author    zhoubing
 * @date      Jul 5, 2011
 * @revision  1.0
 */
package org.jftone.datasource;

import java.util.Properties;
import java.util.Set;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.jftone.config.Const;
import org.jftone.exception.DataSourceException;
import org.jftone.exception.DbException;
import org.jftone.util.StringUtil;

public final class DataSourceLoad {
	public static final String KEY_DATASOURCE = "datasource";
	public static final String KEY_DBTYPE = "databaseType";
	public static final String KEY_CLUSTERENABLED = "clusterEnabled";
	public static final String KEY_CLUSTERDOMAIN = "clusterDomain";
	public static final String KEY_CLUSTERWEIGHT = "weight";
	
	private DataSourceLoad() { 
		super(); 
	}
	
	/**
	 * 初始化
	 * @param jdbcPro
	 * @return 
	 * @throws Exception
	 */
	public static void init(Properties jdbcPro) throws DataSourceException {
		try {
			String dsStr = jdbcPro.getProperty(KEY_DATASOURCE);
			if(!StringUtil.isBlank(dsStr)){
				String[] dsList = dsStr.split(Const.SPLIT_COMMA);
				int i=0;
				for(String key : dsList){
					if( i== 0) {
						DataSourceContext.setDefaultDataSourceName(key);
					}
					DataSourceContext.putDatasource(key, createDataSource(key, getJdbcProperty(jdbcPro, key)));
					i++;
				}
			}else{
				String key = DataSourceContext.getDefaultDataSourceName();
				DataSourceContext.putDatasource(key, createDataSource(key, jdbcPro));
			}
			loadClusterDataSource(jdbcPro);
			
		} catch (Exception e) {
			throw new DataSourceException("数据源初始化错误："+e.getMessage(), e);
		}
	}
	
	private static void loadClusterDataSource(Properties jdbcPro) throws DataSourceException{
		String clusterEnabled = jdbcPro.getProperty(KEY_CLUSTERENABLED);
		if(StringUtil.isBlank(clusterEnabled) || !clusterEnabled.equals("true")){
			return;
		}
		DataSourceContext.enabledCluster();
		String masterStr,slaveStr,mWeightStr,sWeightStr;
		String[] masters,slaves;
		int[] masterWeights, slavesWeights;
		String masterKey,slaveKey;
		String weightSubfix = Const.SYMBOL_POINT+KEY_CLUSTERWEIGHT;
		String clusterDomain = jdbcPro.getProperty(KEY_CLUSTERDOMAIN);
		if(!StringUtil.isBlank(clusterDomain)){
			String[] domainList = clusterDomain.split(Const.SPLIT_COMMA);
			int i=0;
			Properties props = null;
			for(String key : domainList){
				if( i== 0) {
					ClusterDataSource.setDefaultClusterName(key);
				}
				ClusterDataSource.addClusterDomain(key);
				masterKey = key + Const.SYMBOL_POINT + ClusterDataSource.KEY_MASTER;
				slaveKey = key + Const.SYMBOL_POINT + ClusterDataSource.KEY_SLAVE;
				props = getJdbcProperty(jdbcPro, key);
				masterStr = props.getProperty(masterKey);
				slaveStr = props.getProperty(slaveKey);
				if(null == props.getProperty(masterKey)){
					throw new DataSourceException(masterKey+"没有配置");
				}
				if(null == props.getProperty(slaveKey)){
					throw new DataSourceException(slaveKey+"没有配置");
				}
				masters = masterStr.split(Const.SPLIT_COMMA);
				slaves = slaveStr.split(Const.SPLIT_COMMA);
				
				ClusterDataSource.putDbCluster(masterKey, masters);
				ClusterDataSource.putDbCluster(slaveKey, slaves);
				
				//判断是否有主库权重选择
				if(masters.length>1){
					mWeightStr = jdbcPro.getProperty(masterKey+weightSubfix);
					masterWeights = getClusterWeight(masterKey, masters.length, mWeightStr);
					if(null !=masterWeights){
						ClusterDataSource.putDbWeight(masterKey, masterWeights);
					}
				}
				//判断是否有从库权重选择
				if(slaves.length>1){
					sWeightStr = jdbcPro.getProperty(slaveKey+weightSubfix);
					slavesWeights = getClusterWeight(slaveKey, slaves.length, sWeightStr);
					if(null !=slavesWeights){
						ClusterDataSource.putDbWeight(slaveKey, slavesWeights);
					}
				}
				
				i++;
			}
		}else{
			String key = ClusterDataSource.getDefaultClusterName();
			ClusterDataSource.addClusterDomain(key);
			
			masterKey = key+Const.SYMBOL_POINT+ClusterDataSource.KEY_MASTER;
			slaveKey = key+Const.SYMBOL_POINT+ClusterDataSource.KEY_SLAVE;		
			
			masterStr = jdbcPro.getProperty(ClusterDataSource.KEY_MASTER);
			slaveStr = jdbcPro.getProperty(ClusterDataSource.KEY_SLAVE);
			
			masters = null != masterStr ? masterStr.split(Const.SPLIT_COMMA) : new String[]{ClusterDataSource.KEY_MASTER};
			slaves = null != slaveStr ? slaveStr.split(Const.SPLIT_COMMA) : new String[]{ClusterDataSource.KEY_SLAVE};
			
			ClusterDataSource.putDbCluster(masterKey, masters);
			ClusterDataSource.putDbCluster(slaveKey, slaves);
			
			//判断是否有主库权重选择
			if(masters.length>1){
				mWeightStr = jdbcPro.getProperty(ClusterDataSource.KEY_MASTER+weightSubfix);
				masterWeights = getClusterWeight(ClusterDataSource.KEY_MASTER, masters.length, mWeightStr);
				if(null !=masterWeights){
					ClusterDataSource.putDbWeight(masterKey, masterWeights);
				}
			}
			//判断是否有从库权重选择
			if(slaves.length>1){
				sWeightStr = jdbcPro.getProperty(ClusterDataSource.KEY_SLAVE+weightSubfix);
				slavesWeights = getClusterWeight(ClusterDataSource.KEY_SLAVE, slaves.length, sWeightStr);
				if(null !=slavesWeights){
					ClusterDataSource.putDbWeight(slaveKey, slavesWeights);
				}
			}
		}
	}
	
	/**
	 * 获取相关Properties对象
	 * @param jdbcPro
	 * @param filterKey
	 * @return
	 */
	private static Properties getJdbcProperty(Properties jdbcPro, String filterKey) {
		if(filterKey.endsWith(Const.SYMBOL_POINT)){
			filterKey += Const.SYMBOL_POINT;
		}
		Set<String> jdbcs = jdbcPro.stringPropertyNames();
		Properties jdbc = new Properties();
		int start = filterKey.length()+1;
		for(String key : jdbcs){
			if(!key.startsWith(filterKey)) {
				continue;
			}
			jdbc.setProperty(key.substring(start), jdbcPro.getProperty(key));
		}
		return jdbc;
	}
	
	/**
	 * 获取集群对应的权重比数组
	 * @param clusterSize
	 * @param weightStr
	 * @return
	 */
	private static int[] getClusterWeight(String clusterKey, int clusterSize, String weightStr) throws DataSourceException{
		int[] weights = null;
		if(null == weightStr || weightStr.equals("")){
			return weights;
		}
		weights = new int[clusterSize];
		String[] weightStrs =  weightStr.split(Const.SPLIT_COMMA);
		if(weightStrs.length != clusterSize){
			throw new DataSourceException(clusterKey+"数据源权重比个数不对应");
		}
		for(int i = 0; i<clusterSize; i++){
			if(!StringUtil.isNumber(weightStrs[i])){
				throw new DataSourceException(clusterKey+"数据源权重数值必须为整数");
			}
			weights[i] = Integer.parseInt(weightStrs[i]);
		}
		return weights;
	}
	
	/**
	 * 创建数据源连接池
	 * @param key
	 * @param jdbcPro
	 * @return
	 * @throws Exception
	 */
	private static BasicDataSource createDataSource(String key, Properties jdbcPro) throws Exception {
		String dbTypeStr = jdbcPro.getProperty(KEY_DBTYPE);
		DBType dbType = DBType.getDBType(dbTypeStr);
		if(null == dbType){
			throw new DbException("数据库类型没有设置或不支持的数据库类型");
		}
		DataSourceContext.putDbType(key, dbType);		//记录数据库类型
		Class.forName(dbType.getDriverName());
		return BasicDataSourceFactory.createDataSource(jdbcPro);
			
	}
}
