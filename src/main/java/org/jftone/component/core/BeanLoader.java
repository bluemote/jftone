package org.jftone.component.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jftone.annotation.Autowired;
import org.jftone.annotation.Configuration;
import org.jftone.annotation.DataSource;
import org.jftone.annotation.Resource;
import org.jftone.component.BeanContext;
import org.jftone.component.BeanInterceptor;
import org.jftone.config.Const;
import org.jftone.dao.Dao;
import org.jftone.dao.DaoContext;
import org.jftone.exception.CommonException;
import org.jftone.exception.ComponentException;
import org.jftone.service.BaseService;
import org.jftone.util.DataMap;
import org.jftone.util.FileUtil;
import org.jftone.util.IData;
import org.jftone.util.ObjectUtil;
import org.jftone.util.StringUtil;

import net.sf.cglib.proxy.Enhancer;

@SuppressWarnings("deprecation")
abstract class BeanLoader {
	private static Log log = LogFactory.getLog(BeanLoader.class);
	
	protected Class<?> beanClazz;
	
	<T> BeanLoader(Class<T> beanClazz){
		this.beanClazz = beanClazz;
	}
	
	/**
	 * 解析bean，并实例化bean
	 * @param beanClazz
	 */
	abstract <T> boolean parseClazz() throws ComponentException;
	
	/**
	 * 
	 * @param beanClazz
	 * @param setterPro
	 * @return
	 * @throws ComponentException
	 */
	@SuppressWarnings("unchecked")
	<T> T createBean(Class<T> beanClazz, boolean setterPro) throws ComponentException {
		//创建代理对象
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(beanClazz);
		enhancer.setCallback(new BeanInterceptor<T>(beanClazz));
		enhancer.setClassLoader(beanClazz.getClassLoader());
		T componentObj = (T) enhancer.create();
		if(setterPro){
			//设置依赖属性
			setProperty(beanClazz, componentObj);
		}
		return componentObj;
	}
	/**
	 * 实例化Bean
	 * @param beanClazz
	 * @return
	 * @throws ComponentException
	 */
	<T> T createBean(Class<T> beanClazz) throws ComponentException {
		return createBean(beanClazz, true);
	}
	
	/**
	 * 根据属性注解注入组件依赖对象
	 * @param beanClazz
	 * @param serviceInstance
	 * @throws ComponentException
	 */
	<T> void setProperty(Class<T> beanClazz, T serviceInstance) throws ComponentException {
		try {
			Field[] fields = beanClazz.getDeclaredFields();
			//判断是否有Dao，并自动注入
			for(Field field : fields){
				if(field.isAnnotationPresent(DataSource.class)){
					doSetDataSource(beanClazz, field, serviceInstance, false);
				}else if(field.isAnnotationPresent(Autowired.class)){
					doAutowired(beanClazz, field, serviceInstance);
				}else if(field.isAnnotationPresent(Resource.class)){
					doSetResouce(beanClazz, field, serviceInstance);
				}else if(field.isAnnotationPresent(Configuration.class)){
					doSetConfiguration(beanClazz, field, serviceInstance);
				}
			}
		}catch(Exception e) {
			log.error("注入组件依赖对象错误", e);
			throw new ComponentException("注入组件依赖对象错误", e);
		}	
	}
	/**
	 * 注入Dao对象
	 * @param beanClazz
	 * @param field
	 * @param serviceInstance
	 * @param autowired		是否自动注入Dao
	 * @throws ComponentException
	 */
	<T> void doSetDataSource(Class<T> beanClazz, Field field, T serviceInstance, boolean autowired) throws ComponentException {
		try {
			if(autowired){
				Autowired ds = field.getAnnotation(Autowired.class);
				field.setAccessible(true);
				field.set(serviceInstance, DaoContext.createDao(ds.value()));
				return;
			}
			DataSource ds = field.getAnnotation(DataSource.class);
			
			Method method = beanClazz.getMethod(ObjectUtil.getSetter(field.getName()), Dao.class);
			method.invoke(serviceInstance, DaoContext.createDao(ds.value()));
			method = null;
			
			/**
			 * 如果Service继承BaseService则需要注入dao对象，兼容以前老版本
			 */
			if(beanClazz.getSuperclass() == BaseService.class){
				method = beanClazz.getMethod(Const.DAO_METHOE, new Class[]{Dao.class});
		    	method.invoke(serviceInstance, new Object[]{DaoContext.createDao()});
			}
		} catch (NoSuchMethodException | SecurityException | 
				IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			log.error("注入Dao对象错误", e);
			throw new ComponentException("注入Dao对象错误", e);
		}
	}
	
	/**
	 * 注入其他依赖资源
	 * @param beanClazz
	 * @param fields
	 * @throws CommonException 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	<T> void doSetResouce(Class<T> beanClazz, Field field, T serviceInstance) throws ComponentException {
		Class<T> fieldClazz = null;
		String className = null;
		try {
			fieldClazz = (Class<T>)field.getType();
			className = fieldClazz.getName();
			if(!BeanContext.hasBean(fieldClazz)) {
				//如果依赖的Bean没有进行实例化解析，则需要校验类是否
				BeanLoaderManager.doParseClazz(fieldClazz, true);	
			}
			Method method = beanClazz.getMethod(ObjectUtil.getSetter(field.getName()), fieldClazz);
			method.invoke(serviceInstance, BeanContext.getBean(fieldClazz));
			method = null;
		}catch(Exception e) {
			log.error("注入依赖的bean组件["+className+"]错误", e);
			throw new ComponentException("注入依赖的bean组件["+className+"]错误", e);
		}
	}

	/**
	 * 自动注入属性
	 * @param beanClazz
	 * @param field
	 * @param serviceInstance
	 * @throws ComponentException
	 */
	<T> void doAutowired(Class<T> beanClazz, Field field, T serviceInstance) throws ComponentException {
		try {
			@SuppressWarnings("unchecked")
			Class<T> fieldClazz = (Class<T>)field.getType();
			if(fieldClazz.isAssignableFrom(Dao.class)){
				doSetDataSource(beanClazz, field, serviceInstance, true);
				return;
			}
			if(!BeanContext.hasBean(fieldClazz)) {
				//如果依赖的Bean没有进行实例化解析，则需要校验类是否
				BeanLoaderManager.doParseClazz(fieldClazz, true);	
			}
			field.setAccessible(true);
			field.set(serviceInstance, BeanContext.getBean(fieldClazz));
		}catch(Exception e) {
			log.error("注入依赖的bean组件错误", e);
			throw new ComponentException("注入依赖的bean组件错误", e);
		}
	}

	/**
	 * 注入配置
	 * @param beanClazz
	 * @param fields
	 * @throws CommonException 
	 * @throws Exception
	 */
	<T> void doSetConfiguration(Class<T> beanClazz, Field field, T serviceInstance) throws ComponentException {
		Configuration conf = field.getAnnotation(Configuration.class);
		String file = conf.file();
		String prefix = conf.prefix();
		try {
			String ends = file.substring(file.lastIndexOf(".")+1).toLowerCase();
			IData<String, Object> tmpData = null;
			if(ends.endsWith(Const.CONFIG_XML)) {
				tmpData = FileUtil.loadClasspathXMLData(file);
			}else {
				tmpData = FileUtil.loadClasspathPropsData(file);
			}
			if(null == tmpData || tmpData.isEmpty()) {
				throw new Exception("配置注解属性对应文件解析数据为空");
			}
			IData<String, Object> props = new DataMap<>();
			if(!StringUtil.isBlank(prefix)) {
				String key = null;
				for(Map.Entry<String, Object> entry : tmpData.entrySet()) {
					key = entry.getKey();
					props.put(key.substring(key.indexOf(prefix)+prefix.length()+1), entry.getValue());
	    		}
			}else {
				props = tmpData;
			}
			@SuppressWarnings("unchecked")
			Class<T> fieldClazz = (Class<T>)field.getType();
			Method method = beanClazz.getMethod(ObjectUtil.getSetter(field.getName()), fieldClazz);
			if(!fieldClazz.isAssignableFrom(DataMap.class)) {
				throw new Exception("配置注解属性数据类型错误，只能为IData");
			}
			method.invoke(serviceInstance, props);
			method = null;
		}catch(Exception e) {
			log.error("注入依赖的bean组件错误", e);
			throw new ComponentException("注入依赖的bean组件错误", e);
		}
	}
	
}
