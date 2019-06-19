package org.jftone.action;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jftone.action.render.RenderFactory;
import org.jftone.config.Const;
import org.jftone.config.PropertyConfigurer;
import org.jftone.session.HttpServletReqWrapper;
import org.jftone.session.HttpServletRespWrapper;
import org.jftone.session.HttpSessionRepository;

public final class JFToneFilter implements Filter {
	private Logger log = LoggerFactory.getLogger(JFToneFilter.class);
	protected ServletContext context;
	private ActionHandler handler;
	private int contextPathLen;
	private final String LINE = "/";
	private boolean sessionSharingTag = false;
	private Map<String, ActionMapping> actionMap = null;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		request.setCharacterEncoding(Const.CHARSET_UTF8);
		//启用session共享
		if(sessionSharingTag){
			request = new HttpServletReqWrapper(request);
			response = new HttpServletRespWrapper(request, response);
		}
		boolean handled = false;
		String actionURI = request.getRequestURI();
		try {
			if (contextPathLen != 0) actionURI = actionURI.substring(contextPathLen);
			if(!LINE.equals(actionURI)){
				handled = true;
				int idx = actionURI.indexOf('.');
				if (idx != -1) {
					String urlPartern = PropertyConfigurer.get(PropertyConfigurer.URL_PARTERN);
					if(urlPartern.contains(actionURI.substring(idx+1))){
						actionURI = actionURI.substring(0, idx);
					}else{
						handled = false;
					}
				}else if(actionURI.endsWith(LINE)){			//以 / 结果情况，去掉重新解析
					actionURI = actionURI.substring(0, actionURI.length()-1);
				}
			}
			if(handled){
				ActionMapping actionMapping = actionMap.get(actionURI);
				if(actionMapping != null){
					handler.doAction(context, actionMapping, request, response);
				}else {
					//找不到地址传递出去
					handled = false;
				}
			}
		} catch (Exception e) {
			String qs = request.getQueryString();
			log.error("执行action错误："+(qs == null ? request.getRequestURL() : request.getRequestURL() + "?" + qs),  e);
		}finally{
			//如果不匹配，则把filter链传递出去
			if(!handled){
				chain.doFilter(request, response);
			}
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		context = config.getServletContext();
		try {
			ActionContext actContext = new ActionContext();
			actContext.loadRouteMapping(config.getInitParameter("config"));
			
			//加载国际化资源数据
			ResourceContext resContext = new ResourceContext();
			resContext.loadResource(PropertyConfigurer.get(PropertyConfigurer.I18N_RESOURCE));
			actionMap = actContext.getActionMapping();
			context.setAttribute(ActionHandler.ACTION_INTERCEPTOR, actContext.getActionInterceptor());
			context.setAttribute(ActionHandler.ACTION_RESOURCE, resContext);
			
			//初始化Freemarker模板配置
			RenderFactory.init(context);
			
			handler = ActionHandler.getHandler();
			
			String contextPath = context.getContextPath();
			contextPathLen = (contextPath == null || LINE.equals(contextPath) ? 0 : contextPath.length());
			
			String sessionSharing = PropertyConfigurer.get(PropertyConfigurer.SESSION_SHARING);
			sessionSharingTag = (sessionSharing == null || sessionSharing.equals("")) ? false : true;
			if(sessionSharingTag){
				HttpSessionRepository.getInstance().initParam(sessionSharing);
			}

		} catch (Exception e) {
			log.error("Action初始化错误", e);
			throw new ServletException("Action初始化错误", e);
		}
	}
	
	@Override
	public void destroy() {
		actionMap.clear();
		actionMap = null;
		context.removeAttribute(ActionHandler.ACTION_INTERCEPTOR);
		context.removeAttribute(ActionHandler.ACTION_RESOURCE);
	}
	
}
