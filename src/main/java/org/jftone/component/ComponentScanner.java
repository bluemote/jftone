/**
 * ComponentScanner.java
 * 扫描service文件加载对象关系映射
 * 
 * @author		zhoubing
 * @date   		Dec 29, 2016
 * @revision	v1.0
 */
package org.jftone.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jftone.annotation.Aspect;
import org.jftone.annotation.Component;
import org.jftone.annotation.Controller;
import org.jftone.annotation.Service;
import org.jftone.component.core.BeanLoaderManager;
import org.jftone.config.Const;
import org.jftone.exception.ComponentException;
import org.jftone.util.ClassUtil;

public final class ComponentScanner {
	private Log log = LogFactory.getLog(ComponentScanner.class);
	
	private List<Class<?>> beanClazzs = new ArrayList<>();
	private List<Class<?>> aopClazzs = new ArrayList<>();
	
	/**
	 * 解析系统组件扫描包
	 * @param packageName
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <T> void parsePackage(String packageName) throws Exception{
		String[] pkgNames = null;
		if(packageName.contains(Const.SPLIT_COMMA)){
			packageName = "org.jftone.service,"+packageName;
			pkgNames = packageName.split(Const.SPLIT_COMMA);
		}else{
			pkgNames = new String[]{"org.jftone.service",packageName}; 
		}
		for(String pn : pkgNames){
			loadService(pn);
		}
		//初始化Bean类
		if(!beanClazzs.isEmpty()){
			//setter类属性
			for(Class<?> clazz : beanClazzs){
				Class<T> beanClazz = (Class<T>)clazz;
				T beanObject = BeanContext.getBean(beanClazz);
				BeanLoaderManager.doSetterBean(beanClazz, beanObject);
			}
			
		}
		//初始化AOP类
		if(!aopClazzs.isEmpty()){
			//setter类属性
			for(Class<?> clazz : aopClazzs){
				Class<T> aopClazz = (Class<T>)clazz;
				T beanObject = BeanContext.getBean(aopClazz);
				BeanLoaderManager.doSetterBean(aopClazz, beanObject);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	<T> void loadService(String packageName) throws Exception{
		List<String> classes = ClassUtil.getClasses(packageName);
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		for(String clazzName : classes){
			Class<T> clazz = (Class<T>) classLoader.loadClass(clazzName);
			if(null == clazz || clazz.getDeclaredAnnotations().length<1) {
				continue;
			}
			if(clazz.isAnnotationPresent(Component.class) ||
					clazz.isAnnotationPresent(Service.class)) {
				//如果是单例的bean，为了解决相互依赖导致初始化失败，依赖注入延迟执行
				if(initBeanObject(clazz)){
					beanClazzs.add(clazz);
				}
			}else if(clazz.isAnnotationPresent(Aspect.class)) {
				initBeanObject(clazz);
				aopClazzs.add(clazz);
			}else if(clazz.isAnnotationPresent(Controller.class)) {
				//Controller没有任何依赖，直接解析
				initBeanObject(clazz);
			}
		}
	}
	
	/**
	 * 初始化class
	 * @param beanClazz
	 * @throws ComponentException
	 */
	private <T> boolean initBeanObject(Class<T> beanClazz) throws ComponentException {
		boolean singleton = false;
		String clazzName = beanClazz.getName();
		try{
			singleton = BeanLoaderManager.doParseClazz(beanClazz);
			log.debug("实例化服务："+clazzName);
		}catch(Exception e){
			throw new ComponentException("实例化服务：["+clazzName+"]错误", e);
		}
		return singleton;
	}
	
}