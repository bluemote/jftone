package org.jftone.component.core;

interface BeanLoaderBuilder {
	<T> BeanLoader createBeanLoader(Class<T> beanClazz);
}
