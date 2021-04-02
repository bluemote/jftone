package org.jftone.component.core;

import java.lang.annotation.Annotation;

import org.jftone.annotation.Aspect;
import org.jftone.annotation.Component;
import org.jftone.annotation.Controller;
import org.jftone.annotation.Service;

enum BeanEnum implements BeanLoaderBuilder{
	COMPONENT(Component.class){
		@Override
		public <T> BeanLoader createBeanLoader(Class<T> beanClazz) {
			//创建通用组件加载解析类实例
			return new ComponentLoader(beanClazz);
		}
	},
	SERVICE(Service.class){
		@Override
		public <T> BeanLoader createBeanLoader(Class<T> beanClazz) {
			//创建服务层组件加载解析类实例
			return new ServiceLoader(beanClazz);
		}
	},
	CONTROLLER(Controller.class){
		@Override
		public <T> BeanLoader createBeanLoader(Class<T> beanClazz) {
			//创建控制层组件加载解析类实例
			return new ControllerLoader(beanClazz);
		}
	},
	ASPECT(Aspect.class){
		@Override
		public <T> BeanLoader createBeanLoader(Class<T> beanClazz) {
			//创建控制层组件加载解析类实例
			return new AspectLoader(beanClazz);
		}
	};
	
	private Class<? extends Annotation> clazz;
	
	/**
	 * 购物在
	 * @param clazz
	 */
	BeanEnum(Class<? extends Annotation> clazz){
		this.clazz = clazz;
	}
	
	Class<? extends Annotation> getClazz(){
		return this.clazz;
	}
	
	/**
	 * 判断代入的注解是否符合枚举类型
	 * @param beanClazz
	 * @return
	 */
	boolean supperted(Class<? extends Annotation> beanClazz){
		return this.clazz.isAssignableFrom(beanClazz);
	}
}
