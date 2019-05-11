package org.jftone.aop;

public class AspectAdvisor {
	
	private AdviseIndicator adviseIndicator;
	
	public final AdviseIndicator getAdviseIndicator() {
		return adviseIndicator;
	}
	public final void setAdviseIndicator(AdviseIndicator adviseIndicator) {
		this.adviseIndicator = adviseIndicator;
	}
	/**
	 * 在目标方法被调用前执行
	 * @param joinPoint
	 */
	public void before(JoinPoint joinPoint) {}
	public void before() {}
	/**
	 * 在目标方法被调用后执行
	 * @param joinPoint
	 */
	public void after(JoinPoint joinPoint) {}
	public void after() {}
	/**
	 * 在目标方法调用前后环绕执行
	 * @param proceedJoinPoint
	 */
	public void around(ProceedJoinPoint proceedJoinPoint) throws Throwable {}
	/**
	 * 在目标方法调用抛错时执行
	 * @param joinPoint
	 * @param error		抛错exception
	 */
	public void throwing(JoinPoint joinPoint, Throwable error) {}
	public void throwing() {}
	/**
	 * 在目标方法正常执行没有抛错时执行
	 * @param joinPoint
	 * @param retVal	返回值
	 */
	public void afterReturning(JoinPoint joinPoint, Object retVal) {}
	public void afterReturning() {}
}
