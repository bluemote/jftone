package org.jftone.aop;

import java.lang.reflect.Method;

public class JoinPoint {
	private Object proxy;
	private Class<?> targetClass;
	private Method method;
	private Object[] params;
	
	public Object getProxy() {
		return proxy;
	}
	public void setProxy(Object proxy) {
		this.proxy = proxy;
	}
	public Class<?> getClazz() {
		return targetClass;
	}
	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public Object[] getParams() {
		return params;
	}
	public void setParams(Object[] params) {
		this.params = params;
	}
}
