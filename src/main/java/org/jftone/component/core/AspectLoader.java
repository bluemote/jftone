package org.jftone.component.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jftone.annotation.Aspect;
import org.jftone.aop.AopUtil;
import org.jftone.aop.AspectAdvisor;
import org.jftone.aop.CustomizedPointcut;
import org.jftone.component.AspectContext;
import org.jftone.component.BeanContext;
import org.jftone.exception.ComponentException;
import org.jftone.util.StringUtil;

class AspectLoader extends BeanLoader {
	private static Log log = LogFactory.getLog(AspectLoader.class);

	<T> AspectLoader(Class<T> beanClazz) {
		super(beanClazz);
	}

	@Override
	void parseClazz() throws ComponentException {
		String beanName = beanClazz.getName();
		if (!AspectAdvisor.class.isAssignableFrom(beanClazz)) {
			throw new ClassCastException(beanName+"注解Aspect的类必须实现BaseAspect接口");
		}
		Aspect aspect = beanClazz.getAnnotation(Aspect.class);
		String[] classNames = aspect.className();
		String[] methodNames = aspect.methodName();

		if (null == classNames || (classNames.length == 1 && StringUtil.isBlank(classNames[0]))) {
			throw new IllegalArgumentException(beanName+"注解Aspect属性className错误，不能为空字符串");
		}
		if (null == methodNames || (methodNames.length == 1 && StringUtil.isBlank(methodNames[0]))) {
			throw new IllegalArgumentException(beanName+"注解Aspect属性methodName错误，不能为空字符串");
		}

		CustomizedPointcut cp = new CustomizedPointcut();
		cp.setClassNameExpression(AopUtil.parseClassName(beanName, classNames));
		cp.setMethodExpression(AopUtil.parseMethodName(beanName, methodNames));
		cp.setOrder(aspect.order());
		cp.setAllowAnyClass(AopUtil.allowAll(classNames));			//是否允许所有代理类
		cp.setAllowAnyMethod(AopUtil.allowAll(methodNames));		//是否允许所有代理类方法
		try {
			// 实例化AOP通知对象，AOP对象也属于容器管理
			AspectAdvisor aspectAdvisor = (AspectAdvisor) BeanContext.getBean(beanClazz);
			aspectAdvisor.setAdviseIndicator(AopUtil.parseAdviseIndicator(beanClazz.getDeclaredMethods()));
			cp.setAspectAdvisor(aspectAdvisor);
		} catch (Exception e) {
			log.error("实例化[" + beanName + "]错误，必须是public类型无参构造函数", e);
			throw new ComponentException("实例化[" + beanClazz.getName() + "]错误，必须是public类型无参构造函数", e);
		}
		AspectContext.add(cp);
	}

	@Override
	<T> T createBean(Class<T> beanClazz) {
		return null;
	}
	
}
