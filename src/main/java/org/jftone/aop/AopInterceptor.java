package org.jftone.aop;

public interface AopInterceptor {
	
	/**
	 * 调用执行目标代理方法
	 * @param methodInvocation
	 * @return
	 * @throws Throwable
	 */
	public Object invoke(MethodInvocation methodInvocation) throws Throwable;

}
