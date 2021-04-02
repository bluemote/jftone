package org.jftone.component.core;

import org.jftone.annotation.Component;
import org.jftone.component.BeanContext;
import org.jftone.config.Const;
import org.jftone.exception.ComponentException;

class ComponentLoader extends BeanLoader {
	
	<T> ComponentLoader(Class<T> beanClazz) {
		super(beanClazz);
	}

	@Override
	@SuppressWarnings("unchecked")
	<T> boolean parseClazz() throws ComponentException {
		boolean singleton = false;
		final Class<T> clazz = (Class<T>)beanClazz;
		//执行初始化方法
		ComponentBody<T> ch = new ComponentBody<T>();
		Component component = clazz.getAnnotation(Component.class);
		ch.setName(component.name());
		ch.setScope(component.scope());
		ch.setInitMethod(component.init());
		ch.setDestroyMethod(component.destroy());
		if(component.scope().equals(Const.SCOPE_SINGLETON)) {
			T componentObj = createBean(clazz, false);
			ch.setInstance(componentObj);
			singleton = true;
		}else {
			ch.setClazz(clazz);
		}
		BeanContext.setBean(clazz, ch);
		return singleton;
	}
	
}
