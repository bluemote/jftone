package org.jftone.component.core;

import java.util.ArrayList;
import java.util.List;

import org.jftone.annotation.Aspect;
import org.jftone.annotation.Component;
import org.jftone.annotation.Controller;
import org.jftone.annotation.Service;
import org.jftone.component.BeanContext;
import org.jftone.exception.ComponentException;

public final class BeanLoaderManager {
	private static final int ANNOTATION_NUM = 6;
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
	private static <T> List<BeanLoader> getBeanLoader(Class<T> beanClazz, boolean checkAopAdvisor) throws ComponentException {
		if(null == beanClazz || beanClazz.getDeclaredAnnotations().length<1) {
			throw new ComponentException("Class["+beanClazz.getName()+"]没有设置注解");
		}
		List<BeanLoader> beanLoaders = new ArrayList<>(ANNOTATION_NUM);
		if(beanClazz.isAnnotationPresent(Component.class)) {
			beanLoaders.add(BeanLoaderFactory.getBeanLoader(Component.class, beanClazz));
		}else if(beanClazz.isAnnotationPresent(Service.class)) {
			beanLoaders.add(BeanLoaderFactory.getBeanLoader(Service.class, beanClazz));
		}else if(beanClazz.isAnnotationPresent(Controller.class)) {
			beanLoaders.add(BeanLoaderFactory.getBeanLoader(Controller.class, beanClazz));
		}
		if(checkAopAdvisor && beanClazz.isAnnotationPresent(Aspect.class)) {
			beanLoaders.add(BeanLoaderFactory.getBeanLoader(Aspect.class, beanClazz));
		}
		if(beanLoaders.isEmpty()) {
			throw new ComponentException("Class["+beanClazz.getName()+"]类注解不支持");
		}
		return beanLoaders;
	}
	private static <T> BeanLoader getBeanLoader(Class<T> beanClazz) throws ComponentException {
		return getBeanLoader(beanClazz, false).get(0);
	}
	
	/**
	 * 解析组件类，有可能bean上面还存在AOP通知类注解
	 * @param beanClazz
	 * @throws ComponentException
	 */
	public static <T> void doParseClazz(Class<T> beanClazz) throws ComponentException {
		//如果已经初始化，则直接返回
		if(BeanContext.hasBean(beanClazz)) {
			return;
		}
		List<BeanLoader> beanLoaders = getBeanLoader(beanClazz, true);
		for(BeanLoader bl : beanLoaders) {
			bl.parseClazz();
		}
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
