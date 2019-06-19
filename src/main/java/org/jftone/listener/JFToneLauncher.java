package org.jftone.listener;

import java.io.FileNotFoundException;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jftone.code.OrmConfigLoad;
import org.jftone.code.SqlConfigLoad;
import org.jftone.component.BeanContext;
import org.jftone.component.ComponentScanner;
import org.jftone.config.AppListener;
import org.jftone.config.PropertyConfigurer;
import org.jftone.datasource.DataSourceContext;
import org.jftone.datasource.DataSourceLoad;
import org.jftone.jdbc.DBRepository;
import org.jftone.transaction.TransactionManager;
import org.jftone.util.FileUtil;
import org.jftone.util.StringUtil;

public class JFToneLauncher {
	private static Logger log = LoggerFactory.getLogger(JFToneLauncher.class);

	private static final String DATASOURCE_CONFIG = "jdbc.properties";
	
	private static final String JFTAPP_CONFIG = "jftone.properties";
	
	public static void start(String appConfig) {
		try {
			//加载容器配置数据  
			log.error("应用开始启动......");
			if(StringUtil.isBlank(appConfig)){
				appConfig = JFTAPP_CONFIG;
			}
			
			log.error("开始加载"+appConfig+"......");
			Properties appPro = FileUtil.loadClasspathProperties(appConfig);
			if(null == appPro) {
				throw new FileNotFoundException("找不到配置文件："+appConfig);
			}
			PropertyConfigurer.addConfig(appPro);	//保存配置文件		
			
			//加载数据源配置
			String dataSourceConfig = PropertyConfigurer.get(PropertyConfigurer.DATASOURCE_PROPERTIES, DATASOURCE_CONFIG);
			if(!StringUtil.isBlank(dataSourceConfig)){
				log.error("开始加载"+dataSourceConfig+"......");
				Properties jdbcPro = FileUtil.loadClasspathProperties(dataSourceConfig);
				if(null != jdbcPro) {
					log.error("初始化数据源......");
					DataSourceLoad.init(jdbcPro);	//初始化数据源
				}
			}
			
			//解析映射关系数据
			String modelPackage = PropertyConfigurer.get(PropertyConfigurer.MODEL_PACKAGE);
			if(!StringUtil.isBlank(modelPackage)){
				log.error("读取model......");
				OrmConfigLoad ormConfLoad = new OrmConfigLoad();
				ormConfLoad.scanModel(modelPackage);
			}
			
			//加载sql配置
			String sqlConfig = PropertyConfigurer.get(PropertyConfigurer.SQL_CONFIG, SqlConfigLoad.SQL_CONFIG_FILE);
			if(!StringUtil.isBlank(sqlConfig)){
				log.error("读取sql配置......");
				SqlConfigLoad sqlConfLoad = new SqlConfigLoad();
				sqlConfLoad.loadSQL(sqlConfig);
			}
			
			//事务拦截
			String transactional = PropertyConfigurer.get(PropertyConfigurer.TRANSACTIONAL, "");
			if(!StringUtil.isBlank(transactional)){
				log.error("解析事务拦截配置......");
				TransactionManager.createTransactionalPointcut();
			}
			
			//服务类扫描数据
			String componentPackage = PropertyConfigurer.get(PropertyConfigurer.COMPONENT_PACKAGE);
			if(!StringUtil.isBlank(componentPackage)){
				log.error("读取component......");
				ComponentScanner svcConfLoad = new ComponentScanner();
				svcConfLoad.parsePackage(componentPackage);
			}
			
			String appListener = PropertyConfigurer.get(PropertyConfigurer.LISTENE_INTERFACE);
			if(!StringUtil.isBlank(appListener)){
				log.error("初始化服务数据......");
				@SuppressWarnings("unchecked")
				Class<AppListener> appIntfCls =  (Class<AppListener>)Class.forName(appListener);
				appIntfCls.newInstance().load();
			}
			log.error("应用启动完成......");
			
		} catch (Exception ex) {
			log.error("应用启动失败", ex);
		}
	}

	public static void stop() {
		log.error("应用开始注销......");
		try {
			String appListener = PropertyConfigurer.get(PropertyConfigurer.LISTENE_INTERFACE);
			if(null != appListener && !appListener.equals("")){
				log.error("释放应用业务配置......");
				@SuppressWarnings("unchecked")
				Class<AppListener> appIntfCls =  (Class<AppListener>)Class.forName(appListener);
				appIntfCls.newInstance().destroy();
			}
			log.error("释放应用其他配置......");
			//关闭注销Bean
			BeanContext.close();
			
			//注销配置映射
			PropertyConfigurer.destroyed();
			
			DBRepository.destroyed();
			DataSourceContext.destroyed();
			
			log.error("应用注销完成......");
		} catch (Exception ex) {
			log.error("应用注销错误", ex);
		}
	}
}
