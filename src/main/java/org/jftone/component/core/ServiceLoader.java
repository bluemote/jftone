package org.jftone.component.core;

import org.jftone.annotation.Service;
import org.jftone.component.BeanContext;
import org.jftone.component.BeanInterceptor;
import org.jftone.config.Const;
import org.jftone.exception.ComponentException;

import net.sf.cglib.proxy.Enhancer;

class ServiceLoader extends BeanLoader {
	
	<T> ServiceLoader(Class<T> beanClazz) {
		super(beanClazz);
	}
	
	/**
	 * 解析Bean注解配置数据，并缓存起来
	 */
	@Override
	@SuppressWarnings("unchecked")
	<T> void parseClazz() throws ComponentException {
		final Class<T> clazz = (Class<T>)beanClazz;
		ComponentBody<T> ch = new ComponentBody<T>();
		Service service = clazz.getAnnotation(Service.class);
		ch.setName(service.name());
		ch.setScope(service.scope());
		ch.setInitMethod(service.init());
		ch.setDestroyMethod(service.destroy());
		if(service.scope().equals(Const.SCOPE_SINGLETON)) {
			T serviceObj = createBean(clazz);
			ch.setInstance(serviceObj);
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
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(beanClazz);
		enhancer.setCallback(new BeanInterceptor<T>(beanClazz));
		enhancer.setClassLoader(beanClazz.getClassLoader());
		T serviceObj = (T) enhancer.create();
		//设置依赖属性
		setProperty(beanClazz, serviceObj);
		return serviceObj;
	}
}
