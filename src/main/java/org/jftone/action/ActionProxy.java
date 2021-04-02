/**
 * ActionProxy.java
 * Action实例辅助数据
 * 
 * @author		zhoubing
 * @date   		Apr 15, 2012
 * @revision	v1.0
 */
package org.jftone.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jftone.exception.ActionException;

import com.esotericsoftware.reflectasm.MethodAccess;


/**
 * @author zhoubing
 */
final class ActionProxy {
	
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final MessageResource messageResource;
	private final String actionMethod;
	private final Action action;
	private final MethodAccess actionAccess;
	
	public ActionProxy(final HttpServletRequest request, final HttpServletResponse response,
			final Action action, final MethodAccess actionAccess,
			final String actionMethod, final MessageResource messageResource){
		this.request = request;
		this.response = response;
		this.action = action;
		this.actionAccess = actionAccess;
		this.actionMethod = actionMethod;
		this.messageResource = messageResource;
	}
	/**
	 * 返回Request对象
	 * @return
	 */
	public HttpServletRequest getRequest() {
		return request;
	}
	/**
	 * 返回Response对象
	 * @return
	 */
	public HttpServletResponse getResponse() {
		return response;
	}
	/**
	 * 返回国际化资源
	 * @return
	 */
	public MessageResource getMessageResource() {
		return messageResource;
	}
	/**
	 * 返回Action调用方法
	 * @return
	 */
	public String getActionMethod() {
		return actionMethod;
	}
	
	/**
	 * ReflectASM反射对象
	 * @return
	 */
	public MethodAccess getActionAccess() {
		return actionAccess;
	}
	
	/**
	 * 执行Action对象的具体方法
	 * @return
	 * @throws Exception
	 */
	public void initReqParameter() throws ActionException{
		action.initReqParameter(this);
	}
	
	public void handleRequest() throws ActionException{
		action.handleRequest();
	}
	
	static final class Builder{
		private final HttpServletRequest request;
		private final HttpServletResponse response;
		private Action action;
		private MethodAccess actionAccess;
		private String actionMethod;
		private MessageResource messageResource;
		
		public Builder(final HttpServletRequest request, final HttpServletResponse response){
			this.request = request;
			this.response = response;
		}
		/**
		 * 预载需要执行的Action对象
		 * @param action
		 * @return
		 */
		public Builder action(Action action){
			this.action = action;
			return this;
		}
		/**
		 * ReflectASM反射对象
		 * @param action
		 * @return
		 */
		public Builder actionAccess(MethodAccess actionAccess){
			this.actionAccess = actionAccess;
			return this;
		}
		/**
		 * 设置Action调用的方法，默认为execute
		 * @param actionMethod
		 * @return
		 */
		public Builder actionMethod(String actionMethod){
			this.actionMethod = actionMethod;
			return this;
		}
		/**
		 * 设置国际化资源
		 * @param messageResource
		 * @return
		 */
		public Builder messageResource(MessageResource messageResource){
			this.messageResource = messageResource;
			return this;
		}
		
		public ActionProxy build(){
			return new ActionProxy(request, response, action, 
					actionAccess, actionMethod, messageResource);
		}
	}
}
