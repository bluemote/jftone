package org.jftone.component.core;

import org.jftone.annotation.Component;
import org.jftone.component.BeanContext;
import org.jftone.component.BeanInterceptor;
import org.jftone.config.Const;
import org.jftone.exception.ComponentException;

import net.sf.cglib.proxy.Enhancer;

class ComponentLoader extends BeanLoader {
	
	<T> ComponentLoader(Class<T> beanClazz) {
		super(beanClazz);
	}

	@Override
	@SuppressWarnings("unchecked")
	<T> void parseClazz() throws ComponentException {
		final Class<T> clazz = (Class<T>)beanClazz;
		//执行初始化方法
		ComponentBody<T> ch = new ComponentBody<T>();
		Component component = clazz.getAnnotation(Component.class);
		ch.setName(component.name());
		ch.setScope(component.scope());
		ch.setInitMethod(component.init());
		ch.setDestroyMethod(component.destroy());
		if(component.scope().equals(Const.SCOPE_SINGLETON)) {
			T componentObj = createBean(clazz);
			ch.setInstance(componentObj);
		}else {
			ch.setClazz(clazz);
		}
		BeanContext.setBean(clazz, ch);
	}
	
	/**
	 * 实例化Bean
	 * @param beanClazz
	 * @return
	 * @throws ComponentException
	 */
	@Override
	@SuppressWarnings("unchecked")
	<T> T createBean(Class<T> beanClazz) throws ComponentException {
		//创建代理对象
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(beanClazz);
		enhancer.setCallback(new BeanInterceptor<T>(beanClazz));
		enhancer.setClassLoader(beanClazz.getClassLoader());
		T componentObj = (T) enhancer.create();
		//设置依赖属性
		setProperty(beanClazz, componentObj);
		return componentObj;
	}

}
