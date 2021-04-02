package org.jftone.config;

import java.util.List;

public interface AppConfig{
	/**
	 * 加载action映射路由
	 * @param route
	 */
	public void loadRoute(Route route);
	
	/**
	 * 加载action拦截
	 * @param interceptor
	 */
	public List<ActionInterceptor> loadInterceptor();

}
