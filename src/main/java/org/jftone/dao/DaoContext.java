/**
 * DaoContext.java
 * 为DAO程序处理提供便捷
 * 
 * @author		zhoubing
 * @date   		Jul 8, 2011
 * @revision	v1.0
 */
package org.jftone.dao;

import java.util.HashMap;
import java.util.Map;

import org.jftone.datasource.DataSourceContext;
import org.jftone.datasource.RouteDataSource;
import org.jftone.util.StringUtil;

public final class DaoContext {
	private static Map<String, Dao> daoMap = new HashMap<String, Dao>();
	
	private DaoContext(){}
	
	public static Dao createDao(){
		return createDao(null, false);
	}
	
	public static Dao createDao(String routeDsName){
		return createDao(routeDsName, false);
	}
	
	public static Dao createDao(String routeDsName, boolean cluster){
		if(StringUtil.isBlank(routeDsName)){
			routeDsName = DataSourceContext.getDefaultRouteName(cluster);
		}
		if(null == daoMap || !daoMap.containsKey(routeDsName)){
			Dao dao = new Dao();
			dao.setDatasource(new RouteDataSource(routeDsName, cluster));
			daoMap.put(routeDsName, dao);
		}
		return daoMap.get(routeDsName);
	}
}
