package org.jftone.aop;

import java.lang.reflect.Method;
import java.util.List;

import org.jftone.annotation.Aspect;

import net.sf.cglib.proxy.MethodProxy;

public class MethodInvocation {

	private Object proxy;
	@SuppressWarnings("unused")
	private Method method;
	private Class<?> beanClass;
	private Object[] params;
	private MethodProxy methodProxy;
	private List<AopInterceptor> interceptorChain;
	private int interceptorIndex = 0;
	private int chainSize = 0;
	
	public MethodInvocation(Object proxy, Method method, Object[] params, 
			MethodProxy methodProxy,Class<?> beanClass, List<AopInterceptor> chain) {
		this.proxy = proxy;
		this.method = method;
		this.params = params;
		this.methodProxy = methodProxy;
		this.beanClass = beanClass;
		this.interceptorChain = chain;
		this.chainSize = chain.size();
	}
	
	private Object invoke() throws Throwable {
		return methodProxy.invokeSuper(proxy, params);
	}
	
	/**
	 * 执行目标bean方法调用
	 * @return
	 * @throws Throwable
	 */
	public Object proceed() throws Throwable {
		//如果是AOP注解bean，则不进行拦截
		if(beanClass.isAnnotationPresent(Aspect.class)) {
			return invoke();
		}
		//如果拦截器全部执行完毕，则正式调用目标Bean方法
		if(this.interceptorIndex == this.chainSize) {
			return invoke();
		}
		AopInterceptor interceptorHandler = this.interceptorChain.get(this.interceptorIndex++);
		return interceptorHandler.invoke(this);
	}
}
