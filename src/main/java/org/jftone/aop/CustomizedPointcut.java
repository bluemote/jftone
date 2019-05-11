package org.jftone.aop;

public final class CustomizedPointcut extends AspectPointcut {

	private int order; 						// 代理类拦截执行顺序

	private AspectAdvisor aspectAdvisor; 	// 代理类通知对象

	public void setOrder(int order) {
		this.order = order;
	}

	public void setAspectAdvisor(AspectAdvisor aspectAdvisor) {
		this.aspectAdvisor = aspectAdvisor;
	}

	/**
	 * 返回当前切点执行顺序号
	 * @return
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * 返回当前切点通知对象
	 * @return
	 */
	public AspectAdvisor getAspectAdvisor() {
		return aspectAdvisor;
	}

}
