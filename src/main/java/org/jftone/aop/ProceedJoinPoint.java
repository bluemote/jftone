package org.jftone.aop;

public class ProceedJoinPoint extends JoinPoint {
	private Object returnValue;
	private MethodInvocation methodInvocation;
	
	public ProceedJoinPoint(JoinPoint joinPoint){
		this.setProxy(joinPoint.getProxy());
		this.setMethod(joinPoint.getMethod());
		this.setParams(joinPoint.getParams());
		this.setTargetClass(joinPoint.getClass());
	}
	
	public Object getReturnValue() {
		return returnValue;
	}
	
	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}
	
	public void setMethodInvocation(MethodInvocation methodInvocation) {
		this.methodInvocation = methodInvocation;
	}
	
	public void proceed() throws Throwable {
		this.returnValue = methodInvocation.proceed();
	}
}
