package org.jftone.config;

import java.util.Properties;
import java.util.Set;

import org.jftone.util.DataMap;
import org.jftone.util.IData;

public class PropertyConfigurer {
	//普通配置数据
	public static final String DATASOURCE_PROPERTIES = "datasourceConfig";
	public static final String I18N_RESOURCE = "i18nResource";
	public static final String TEMPLET_ROOT = "templetRoot";
	public static final String PRODUCT_MODE = "productMode";
	public static final String MODEL_PACKAGE = "modelPackage";
	public static final String COMPONENT_PACKAGE ="componentPackage";
	public static final String SQL_CONFIG = "sqlConfig";
	public static final String URL_PARTERN = "urlPartern";
	public static final String LISTENE_INTERFACE = "listenService";
	public static final String CACHE_TYPE = "cacheType";
	
	//transactional
	public static final String TRANSACTIONAL ="transactional";
	public static final String TRANSACTION_CLASS ="transactionClass";
	public static final String TRANSACTION_METHOD ="transactionMethod";
	
	//session
	public static final String SESSION_SHARING = "sessionSharing";
	public static final String SESSION_TIMEOUT = "sessionTimeout";
	public static final String SESSION_PARAMETER = "sessionParameter";
	public static final String SESSION_KEY = "sessionKey";
	public static final String COOKIE_DOMAIN = "cookieDomain";
	
	//cached,nosql
	public static final String REDIS = "redis";						//redis配置文件
	public static final String MEMCACHED = "memcached";				//memcached配置文件
	public static final String MONGODB = "mongodb";					//mongodb配置文件
	
	private static IData<String, String> appConfig = new DataMap<String, String>();

	/**
	 * 保存配置文件中配置定义项
	 * @param appPro
	 */
	public static final void addConfig(Properties appPro){
		Set<String> set = appPro.stringPropertyNames();
		for(String key : set){
			String value = appPro.getProperty(key).trim();
			appConfig.put(key.trim(), value);
		}
	}
	/**
	 * 获取web配置某键值数据
	 * @param key
	 * @return
	 */
	public static final String get(String key){
		return appConfig.get(key);
	}
	public static final String get(String key, String defaultValue){
		String confStr = get(key);
		return confStr != null ? confStr : defaultValue;
	}
	/**
	 * web配置数据
	 * @return
	 */
	public static final IData<String, String> getConfig(){
		return appConfig;
	}
		
	public static void destroyed(){
		appConfig.clear();
		appConfig = null;
	}
}
