package org.jftone.aop;

public final class DefaultInterceptor implements AopInterceptor {
	
	private JoinPoint joinPoint;
	
	private ProceedJoinPoint proceedJoinPoint;
	
	private AspectAdvisor aspectAdvisor;
	
	public DefaultInterceptor(AspectAdvisor aspectAdvisor, JoinPoint joinPoint) {
		this.aspectAdvisor = aspectAdvisor;
		this.joinPoint = joinPoint;
		this.proceedJoinPoint = new ProceedJoinPoint(joinPoint);
	}
	
	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		Object retVal = null;
		AdviseIndicator adviseIndicator = aspectAdvisor.getAdviseIndicator();
		try {
			//执行Before方法
			if(adviseIndicator.hasBefore()) {
				if(adviseIndicator.hasBeforeArg()) {
					aspectAdvisor.before(joinPoint);
				}else {
					aspectAdvisor.before();
				}
			}
			try {
				//执行Around方法
				if(adviseIndicator.hasAround()) {
					this.proceedJoinPoint.setMethodInvocation(methodInvocation);
					aspectAdvisor.around(proceedJoinPoint);
					retVal = proceedJoinPoint.getReturnValue();
				}else {
					retVal = methodInvocation.proceed();
				}
			} finally {
				//执行After方法
				if(adviseIndicator.hasAfter()) {
					if(adviseIndicator.hasAfterArg()) {
						aspectAdvisor.after(joinPoint);
					}else {
						aspectAdvisor.after();
					}
				}
			}
			//执行AfterReturning方法
			if(adviseIndicator.hasAfterReturning()) {
				if(adviseIndicator.hasAfterReturningArg()) {
					aspectAdvisor.afterReturning(joinPoint, retVal);
				}else {
					aspectAdvisor.afterReturning();
				}
			}
			
		} catch (Throwable e) {
			if(adviseIndicator.hasThrowing()) {
				if(adviseIndicator.hasThrowingArg()) {
					aspectAdvisor.throwing(joinPoint, e);
				}else {
					aspectAdvisor.throwing();
				}
			}
			throw e;
		} 
		return retVal;
	}
}
