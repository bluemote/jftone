package org.jftone.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jftone.component.AspectContext;

public class InterceptorFactory {

	private InterceptorFactory() {
		// 根据AspectPointcut数组执行顺序序列进行排序
		AspectContext.sort();
	}

	private static class InterceptorFactoryHolder {
		static InterceptorFactory factory = new InterceptorFactory();
	}

	public static InterceptorFactory getInstance() {
		return InterceptorFactoryHolder.factory;
	}

	/**
	 * 获取当前拦截对象
	 * 
	 * @param proxy
	 * @param method
	 * @param params
	 * @param targetClass
	 * @return
	 */
	public List<AopInterceptor> getInterceptor(Object proxy, Method method, Object[] params, Class<?> targetClass) {
		int cpsNum = 0;
		List<CustomizedPointcut> cps = AspectContext.getAspectPointcuts();
		cpsNum =cps.size();
		//多增加一个拦截器长度，保存事务控制拦截器
		List<AopInterceptor> interceptors = new ArrayList<AopInterceptor>(cpsNum + 1);
		// 载入事务拦截器
		TransactionalPointcut tp = AspectContext.getTransactionalPointcut();
		if (null != tp && tp.isEnabled() && (tp.isAnnotationTransactional(targetClass, method)
		|| tp.isMatchClass(targetClass) && tp.isMatchMethod(method)) ) {
			//满足事务拦截条件，进入事务拦截管理
			interceptors.add(new TransactionalInterceptor(targetClass, method));
		}
		//如果没有配置其他拦截器，则直接返回
		if (cpsNum == 0 || cps.isEmpty()) {
			return interceptors;
		}
		// 其他自定义拦截器
		JoinPoint joinPoint = createJoinPoint(proxy, method, params, targetClass);
		for (CustomizedPointcut cp : cps) {
			if (cp.isMatchClass(targetClass) && cp.isMatchMethod(method)) {
				interceptors.add(new DefaultInterceptor(cp.getAspectAdvisor(), joinPoint));
			}
		}
		return interceptors;
	}

	/**
	 * 组装创建连接点对象
	 * 
	 * @param proxy
	 * @param method
	 * @param params
	 * @param targetClass
	 * @return
	 */
	private JoinPoint createJoinPoint(Object proxy, Method method, Object[] params, Class<?> targetClass) {
		JoinPoint joinPoint = new JoinPoint();
		joinPoint.setProxy(proxy);
		joinPoint.setMethod(method);
		joinPoint.setParams(params);
		joinPoint.setTargetClass(targetClass);
		return joinPoint;
	}
}
