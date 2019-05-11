package org.jftone.aop;

import java.lang.reflect.Method;

import org.jftone.annotation.Transactional;

public final class TransactionalPointcut extends AspectPointcut {

	private boolean enabled; 						//是否启用事务

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * 判断目标代理类是否设置事务注解
	 */
	public boolean isAnnotationTransactional(Class<?> targetClass, Method method) {
		return targetClass.isAnnotationPresent(Transactional.class) || method.isAnnotationPresent(Transactional.class);
	}

}
