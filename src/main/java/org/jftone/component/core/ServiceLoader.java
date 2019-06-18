package org.jftone.component.core;

import org.jftone.annotation.Service;
import org.jftone.component.BeanContext;
import org.jftone.config.Const;
import org.jftone.exception.ComponentException;

class ServiceLoader extends BeanLoader {
	
	<T> ServiceLoader(Class<T> beanClazz) {
		super(beanClazz);
	}
	
	/**
	 * 解析Bean注解配置数据，并缓存起来
	 */
	@Override
	@SuppressWarnings("unchecked")
	<T> boolean parseClazz() throws ComponentException {
		boolean singleton = false;
		final Class<T> clazz = (Class<T>)beanClazz;
		ComponentBody<T> ch = new ComponentBody<T>();
		Service service = clazz.getAnnotation(Service.class);
		ch.setName(service.name());
		ch.setScope(service.scope());
		ch.setInitMethod(service.init());
		ch.setDestroyMethod(service.destroy());
		if(service.scope().equals(Const.SCOPE_SINGLETON)) {
			T serviceObj = createBean(clazz, false);
			ch.setInstance(serviceObj);
			singleton = true;
		}else {
			ch.setClazz(clazz);
		}
		BeanContext.setBean(clazz, ch);
		return singleton;
	}
	
}
