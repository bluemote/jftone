/**
 * ComponentScanner.java
 * 扫描service文件加载对象关系映射
 * 
 * @author		zhoubing
 * @date   		Dec 29, 2016
 * @revision	v1.0
 */
package org.jftone.component;

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
import org.jftone.service.BaseService;
import org.jftone.util.ClassUtil;

public final class ComponentScanner {
	private Log log = LogFactory.getLog(ComponentScanner.class);
	
	/**
	 * 解析系统组件扫描包
	 * @param packageName
	 * @throws Exception
	 */
	public void parsePackage(String packageName) throws Exception{
		String[] pkgNames = null;
		if(packageName.contains(Const.SPLIT_COMMA)){
			pkgNames = packageName.split(Const.SPLIT_COMMA);
		}else{
			pkgNames = new String[]{packageName}; 
		}
		loadBaseService();
		for(String pn : pkgNames){
			loadService(pn);
		}
	}
	/**
	 * 根据包名加载并解析对应的组件类
	 * @throws Exception
	 */
	<T> void loadBaseService() throws Exception{
		try{
			BeanLoaderManager.doParseClazz(BaseService.class);
			log.debug("实例化服务："+BaseService.class.getName());
		}catch(Exception e){
			throw new Exception("实例化服务：["+BaseService.class.getName()+"]错误", e);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	<T> void loadService(String packageName) throws Exception{
		List<String> classes = ClassUtil.getClasses(packageName);
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		for(String clazzName : classes){
			Class<T> clazz = (Class<T>) classLoader.loadClass(clazzName);
			if(!supported(clazz)) {
				continue;
			}
			try{
				BeanLoaderManager.doParseClazz(clazz);
				log.debug("实例化服务："+clazzName);
			}catch(Exception e){
				throw new Exception("实例化服务：["+clazzName+"]错误", e);
			}
		}
	}
	
	/**
	 * 判断是加载bean是否为对应注解类型
	 * @param beanClazz
	 * @return
	 * @throws ComponentException
	 */
	private <T> boolean supported(Class<T> beanClazz) throws ComponentException {
		if(null == beanClazz || beanClazz.getDeclaredAnnotations().length<1) {
			return false;
		}
		if(beanClazz.isAnnotationPresent(Component.class)) {
			return true;
		}
		if(beanClazz.isAnnotationPresent(Service.class)) {
			return true;
		}
		if(beanClazz.isAnnotationPresent(Controller.class)) {
			return true;
		}
		if(beanClazz.isAnnotationPresent(Aspect.class)) {
			return true;
		}
		return false;
	}
	
}