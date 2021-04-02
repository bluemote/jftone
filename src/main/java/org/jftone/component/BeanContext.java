package org.jftone.component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jftone.component.core.BeanLoaderManager;
import org.jftone.component.core.ComponentBody;
import org.jftone.config.Const;
import org.jftone.exception.CommonException;
import org.jftone.exception.ComponentException;

public final class BeanContext {
	//缓存所有Bean实例
	private static ConcurrentMap<String, ComponentBody<?>> beanMap = new ConcurrentHashMap<String, ComponentBody<?>>();
	
	/**
	 * 获取Bean实例，如果不存在，则返回null
	 * @param beanClazz
	 * @return Bean实例
	 * @throws ComponentException
	 */
	public static <T> T getBean(Class<T> beanClazz) throws ComponentException {
		String key = beanClazz.getName();
		if(!beanMap.containsKey(key)) {
			return null;
		}
		T bean = null;
		@SuppressWarnings("unchecked")
		ComponentBody<T> componentHolder = (ComponentBody<T>) beanMap.get(key);
		if(componentHolder.getScope().equals(Const.SCOPE_PROTOTYPE)) {
			bean = createBean(beanClazz);
		}else {
			bean = componentHolder.getInstance();
		}
		return bean;
	}
	/**
	 * 获取Bean实例，如果不存在，则返回null
	 * @param beanName
	 * @return
	 * @throws ComponentException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanName) throws ComponentException {
		Class<T> beanClazz;
		try {
			beanClazz = (Class<T>)Thread.currentThread().getContextClassLoader().loadClass(beanName);
		} catch (ClassNotFoundException e) {
			throw new ComponentException("无法加载"+beanName, e);
		}
		return getBean(beanClazz);
	}
	
	/**
	 * 判断Bean实例是否存在
	 * @param beanClazz
	 * @return true|false
	 */
	public static <T> boolean hasBean(Class<T> beanClazz) {
		return beanMap.containsKey(beanClazz.getName());
	}
	
	/**
	 * 设置并保存Bean实例
	 * @param beanClazz
	 * @param componentHolder
	 * @throws CommonException
	 */
	public static <T> void setBean(Class<T> beanClazz, ComponentBody<T> componentHolder) throws ComponentException {
		//执行初始化方法
		if(componentHolder.getScope().equals(Const.SCOPE_SINGLETON)
				&& !"".equals(componentHolder.getInitMethod())) {
			initBean(beanClazz, componentHolder.getInstance(), componentHolder.getInitMethod());
		}
		beanMap.put(beanClazz.getName(), componentHolder);
	}
	
	public static int getCount(){
		return beanMap.size();
	}
	
	/**
	 * 注销Bean，释放并清空所有Bean实例
	 * @throws ComponentException
	 */
	public static void close() throws ComponentException {
		if(beanMap.isEmpty()) {
			return;
		}
		for(ComponentBody<?> item : beanMap.values()) {
			if(item.getScope().equals(Const.SCOPE_SINGLETON)
					&& !"".equals(item.getDestroyMethod())) {
				destroyBean(item.getInstance(), item.getDestroyMethod());
			}
		}
		beanMap.clear();
		beanMap = null;
	}
	
	/**
	 * 创建Bean
	 * @param beanClazz
	 * @return
	 * @throws ComponentException
	 */
	private static <T> T createBean(Class<T> beanClazz) throws ComponentException {
		return BeanLoaderManager.doCreateBean(beanClazz);
	}
	
	/**
	 * 初始化Bean实例
	 * @param beanClazz
	 * @param serviceInstance
	 * @param methodName
	 * @throws ComponentException 
	 */
	private static <T> void initBean(Class<T> beanClazz,T serviceInstance, String methodName) throws ComponentException {
		try {
			Method method = beanClazz.getMethod(methodName);
			method.invoke(serviceInstance);
			method = null;
		} catch (NoSuchMethodException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException  e) {
			
			throw new ComponentException(e);
		}
		
	}
	/**
	 * 注销Bean实例
	 * @param serviceInstance
	 * @param methodName
	 * @throws ComponentException
	 */
	private static <T> void destroyBean(T serviceInstance, String methodName) throws ComponentException {
		try {
			Method method = serviceInstance.getClass().getMethod(methodName);
			method.invoke(serviceInstance);
			method = null;
		} catch (NoSuchMethodException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException  e) {
			
			throw new ComponentException(e);
		}
		
	}
}
