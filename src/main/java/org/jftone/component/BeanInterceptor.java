package org.jftone.component;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jftone.aop.AopInterceptor;
import org.jftone.aop.AopUtil;
import org.jftone.aop.InterceptorFactory;
import org.jftone.aop.MethodInvocation;
import org.jftone.exception.ServiceException;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class BeanInterceptor<T> implements MethodInterceptor {
	private Log log = LogFactory.getLog(BeanInterceptor.class);

	private Class<T> beanClass;

	public BeanInterceptor(Class<T> beanClass) {
		this.beanClass = beanClass;
	}

	@Override
	public Object intercept(Object proxy, Method method, Object[] params, MethodProxy methodProxy)
			throws ServiceException {
		Object result = null;
		List<AopInterceptor> interceptorChain = null;
		try {
			if(log.isDebugEnabled()) {
				log.debug("进入AOP拦截调用" + beanClass.getName() + "." + method.getName());
			}
			if(AopUtil.isSetDaoMethod(method)) {
				return methodProxy.invokeSuper(proxy, params);
			}
			InterceptorFactory factory = InterceptorFactory.getInstance();
			interceptorChain = factory.getInterceptor(proxy, method, params, beanClass);
			if (null == interceptorChain || interceptorChain.isEmpty()) {
				return methodProxy.invokeSuper(proxy, params);
			} 
			MethodInvocation methodInvocation = new MethodInvocation(proxy, method, params, methodProxy,
					this.beanClass, interceptorChain);
			result = methodInvocation.proceed();
			
		} catch (Throwable e) {
			log.error("执行" + beanClass.getName() + "." + method.getName() + "错误", e);
			throw new ServiceException(e);
		} finally {
			if(null != interceptorChain) {
				interceptorChain.clear();
				interceptorChain = null;
			}
		}
		return result;
	}

}
