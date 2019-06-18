package org.jftone.component.core;

import org.jftone.annotation.Aspect;
import org.jftone.annotation.Component;
import org.jftone.annotation.Controller;
import org.jftone.annotation.Service;
import org.jftone.component.BeanContext;
import org.jftone.exception.ComponentException;

public final class BeanLoaderManager {
	private BeanLoaderManager(){
		super();
	}
	/**
	 * 获取Bean加载类
	 * @param beanClazz
	 * @param checkAopAdvisor	是否解析Aspect注解
	 * @return
	 * @throws ComponentException
	 */
	private static <T> BeanLoader getBeanLoader(Class<T> beanClazz) throws ComponentException {
		if(null == beanClazz || beanClazz.getDeclaredAnnotations().length<1) {
			throw new ComponentException("Class["+beanClazz.getName()+"]没有设置注解");
		}
		BeanLoader beanLoader = null;
		if(beanClazz.isAnnotationPresent(Component.class)) {
			beanLoader = BeanLoaderFactory.getBeanLoader(Component.class, beanClazz);
		}else if(beanClazz.isAnnotationPresent(Service.class)) {
			beanLoader = BeanLoaderFactory.getBeanLoader(Service.class, beanClazz);
		}else if(beanClazz.isAnnotationPresent(Controller.class)) {
			beanLoader = BeanLoaderFactory.getBeanLoader(Controller.class, beanClazz);
		}else if(beanClazz.isAnnotationPresent(Aspect.class)) {
			beanLoader = BeanLoaderFactory.getBeanLoader(Aspect.class, beanClazz);
		}
		if(null == beanLoader) {
			throw new ComponentException("Class["+beanClazz.getName()+"]类注解不支持");
		}
		return beanLoader;
	}
	
	/**
	 * 解析组件类，有可能bean上面还存在AOP通知类注解
	 * @param beanClazz
	 * @throws ComponentException
	 */
	public static <T> boolean doParseClazz(Class<T> beanClazz) throws ComponentException {
		return doParseClazz(beanClazz, false);
	}
	
	public static <T> boolean doParseClazz(Class<T> beanClazz, boolean setter) throws ComponentException {
		//如果已经初始化，则按照非单例模式，直接返回
		if(BeanContext.hasBean(beanClazz)) {
			return false;
		}
		BeanLoader beanLoaders = getBeanLoader(beanClazz);
		return beanLoaders.parseClazz();
	}
	
	/**
	 * 创建Bean实例
	 * @param beanClazz
	 * @return
	 * @throws ComponentException
	 */
	public static <T> T doCreateBean(Class<T> beanClazz) throws ComponentException {
		BeanLoader beanLoader = getBeanLoader(beanClazz);
		if(null == beanLoader) {
			return null;
		}
		return beanLoader.createBean(beanClazz);
	}
	
	/**
	 * 设置Bean依赖关联属性
	 * @param beanClazz
	 * @param serviceInstance
	 * @throws ComponentException
	 */
	public static <T> void doSetterBean(Class<T> beanClazz, T serviceInstance) throws ComponentException {
		BeanLoader beanLoader = getBeanLoader(beanClazz);
		if(null == beanLoader) {
			return;
		}
		beanLoader.setProperty(beanClazz, serviceInstance);
	}
	
}
