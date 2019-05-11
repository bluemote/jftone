package org.jftone.component.core;

import java.lang.annotation.Annotation;

public class BeanLoaderFactory {
	/**
	 * 限制进行实例化
	 */
	private BeanLoaderFactory(){
		super();
	}
	/**
	 * 判断是否为系统支持注解
	 * @param annotationClazz[Component, Service, Controller]
	 * @return true | false
	 */
	public static <T> boolean supported(Class<? extends Annotation> annotationClazz) {
		BeanEnum[] beanEnums = BeanEnum.values();
		for(BeanEnum item : beanEnums) {
			if(item.supperted(annotationClazz)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取对应的组件加载实例
	 * @param annotationClazz 注解类
	 * @param beanClazz bean类
	 * @return BeanLoader
	 */
	public static <T> BeanLoader getBeanLoader(Class<? extends Annotation> annotationClazz, Class<T> beanClazz) {
		BeanLoader loader = null;
		BeanEnum[] beanEnums = BeanEnum.values();
		for(BeanEnum item : beanEnums) {
			if(item.supperted(annotationClazz)) {
				loader = item.createBeanLoader(beanClazz);
				break;
			}
		}
		return loader;
	}
}
