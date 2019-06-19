/**
 * ActionServlet.java
 * 所有http请求控制处理入口
 * 需要在web.xml文件中配置
 * 
 * @author		zhoubing
 * @date   		Mar 28, 2012
 * @revision	v1.0
 */
package org.jftone.action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jftone.annotation.Controller;
import org.jftone.component.ControllerContext;
import org.jftone.config.ActionInterceptor;
import org.jftone.exception.ActionException;

/**
 * @author zhoubing
 * 
 */
final class ActionHandler {

	private Logger log = LoggerFactory.getLogger(ActionHandler.class);

	public static final String ACTION_INTERCEPTOR = "ACTION_INTERCEPTOR";
	public static final String ACTION_RESOURCE = "ACTION_RESOURCE";
	public static final String ACTION_METHOD_KEY = "method";
	public static final String ACTION_METHOD = "execute";
	
	private static ActionHandler handler = null;
	
	private ActionHandler(){}
	
	public static ActionHandler getHandler(){
		if(handler == null){
			handler = new ActionHandler();
		} 
		return handler;
	}

	/**
	 * 统一控制逻辑处理
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doAction(ServletContext context, ActionMapping actionMapping, final HttpServletRequest request, final HttpServletResponse response){
		Action action = null;
		String methodName = null;
		Object interceptorObj = context.getAttribute(ACTION_INTERCEPTOR);
		try {
			methodName = actionMapping.getMethodName();
			if(null == methodName || "".equals(methodName)){
				methodName = request.getParameter(ACTION_METHOD_KEY);
				if(null == methodName || "".equals(methodName)){
					methodName = ACTION_METHOD;
				}
			}
			Class<? extends Action> actionClazz = actionMapping.getActionClass();
			action = actionClazz.newInstance(); 
			if(actionClazz.isAnnotationPresent(Controller.class)){
				ControllerContext.doSetterBean(actionClazz, action);
			}
			
			// 创建代理Action
			ActionProxy actionProxy = new ActionProxy.Builder(request, response) 
					.action(action)
					.actionAccess(actionMapping.getMethodAccess())
					.actionMethod(methodName)
					.messageResource(new MessageResource((ResourceContext) context.getAttribute(ACTION_RESOURCE)))
					.build();
			
			//初始化相关参数
			actionProxy.initReqParameter();
			//action前执行，拦截返回false，则程序终止至执行
			if(intercepteBefore(interceptorObj, action, methodName, request, response)){
				actionProxy.handleRequest();
				//action后执行
				intercepteAfter(interceptorObj, action, methodName, request, response);
			}
		} catch (Exception e) {
			log.error("执行action错误",  e);
			try {
				intercepteThrow(interceptorObj, action, methodName, request, response, new ActionException(e));
			} catch (Exception ex) {
				log.error("执行action抛错拦截错误", ex);
			}
		}
	}
	
	/**
	 * action拦截
	 * @param action
	 * @param actionMethod
	 * @param request
	 * @param method
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private boolean intercepteBefore(Object interceptorObj, Action action, String actionMethod, HttpServletRequest request, HttpServletResponse response) throws ActionException {
		boolean retuslt = true;
		if(null == interceptorObj) return retuslt;
		try{
			List<ActionInterceptor> list = (List<ActionInterceptor>)interceptorObj;
			if(list.size()==0) return retuslt;
			for(ActionInterceptor interceptor : list){
				if(!interceptor.before(action, actionMethod, request, response)){
					retuslt = false;
					break;
				}
			}
		}catch(Exception e){
			log.error("执行 action[before]拦截报错："+e.getMessage(), e);
			throw new ActionException("执行action[before]拦截报错："+e.getMessage(), e);
		}
		return retuslt;
	}
	@SuppressWarnings("unchecked")
	private boolean intercepteAfter(Object interceptorObj, Action action, String actionMethod, HttpServletRequest request, HttpServletResponse response) throws ActionException {
		boolean retuslt = true;
		if(null == interceptorObj) return retuslt;
		try{
			List<ActionInterceptor> list = (List<ActionInterceptor>)interceptorObj;
			if(list.size()==0) return retuslt;
			for(ActionInterceptor interceptor : list){
				if(!interceptor.after(action, actionMethod, request, response)){
					retuslt = false;
					break;
				}
			}
		}catch(Exception e){
			log.error("执行 action[after]拦截报错："+e.getMessage(), e);
			throw new ActionException("执行action[after]拦截报错："+e.getMessage(), e);
		}
		return retuslt;
	}
	
	/**
	 * action抛错拦截
	 * @param action
	 * @param actionMethod
	 * @param request
	 * @param ex
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private boolean intercepteThrow(Object interceptorObj, Action action, String actionMethod, HttpServletRequest request, HttpServletResponse response, ActionException ex) {
		boolean retuslt = true;
		if(null == interceptorObj) return retuslt;
		List<ActionInterceptor> list = (List<ActionInterceptor>)interceptorObj;
		if(list.size()==0) return retuslt;
		for(ActionInterceptor interceptor : list){
			if(!interceptor.throwable(action, actionMethod, request, response, ex)){
				retuslt = false;
				break;
			}
		}
		return retuslt;
	}
	
}
