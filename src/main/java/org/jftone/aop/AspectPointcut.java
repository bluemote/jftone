package org.jftone.aop;

import java.lang.reflect.Method;

import org.jftone.util.StringUtil;

public class AspectPointcut {

	private boolean allowAnyClass = false; // 允许任何代理类

	private boolean allowAnyMethod = false; // 允许任何代理类方法

	private String classNameExpression; // 允许代理类的正则表达式

	private String methodExpression; // 允许代理类方法的正则表达式

	public boolean isAllowAnyClass() {
		return allowAnyClass;
	}

	public void setAllowAnyClass(boolean allowAnyClass) {
		this.allowAnyClass = allowAnyClass;
	}

	public boolean isAllowAnyMethod() {
		return allowAnyMethod;
	}

	public void setAllowAnyMethod(boolean allowAnyMethod) {
		this.allowAnyMethod = allowAnyMethod;
	}

	public void setClassNameExpression(String classNameExpression) {
		this.classNameExpression = classNameExpression;
	}

	public void setMethodExpression(String methodExpression) {
		this.methodExpression = methodExpression;
	}

	/**
	 * 是否匹配指定的类名
	 * 
	 * @param targetClass
	 * @return
	 */
	public boolean isMatchClass(Class<?> targetClass) {
		return this.allowAnyClass || (null != this.classNameExpression
				&& StringUtil.find(targetClass.getName(), this.classNameExpression));
	}

	/**
	 * 是否匹配指定的类方法名
	 * 
	 * @param method
	 * @return
	 */
	public boolean isMatchMethod(Method method) {
		return this.allowAnyMethod || (null != this.methodExpression 
				&& StringUtil.find(method.getName(), this.methodExpression));
	}

}
