package org.jftone.component.core;

import org.jftone.action.Action;
import org.jftone.annotation.Controller;
import org.jftone.component.ControllerContext;

class ControllerLoader extends BeanLoader {
	
	<T> ControllerLoader(Class<T> beanClazz) {
		super(beanClazz);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	<T> void parseClazz() {
		if(!Action.class.isAssignableFrom(beanClazz)) {
			throw new ClassCastException("Controlller注解只能配置在Action类上");
		}
		Controller controller = beanClazz.getAnnotation(Controller.class);
		ControllerContext.set(controller.mapping(), (Class<? extends Action>)beanClazz);
	}
	
	@Override
	<T> T createBean(Class<T> beanClazz) {
		return null;
	}

}
