package org.jftone.config;

import java.util.HashMap;
import java.util.Map;

import org.jftone.action.Action;

public class Route {
	private Map<String, Class<? extends Action>> routeMap = new HashMap<String, Class<? extends Action>>(); 
	
	public Route(){
		config();
	}
	/**
	 * 扩展自己定义路由配置
	 */
	public void config(){
		
	}
	/**
	 * Action 路由初始化方法
	 * @param route
	 */
	public final void add(Route route){
		routeMap.putAll(route.getRouteMap());
	}
	
	public final void putAll(Map<String, Class<? extends Action>> mappingMap){
		routeMap.putAll(mappingMap);
	}
	
	/**
	 * 添加Action路由信息 
	 * @param actionKey
	 * @param actionCls
	 */
	public final void add(String actionKey, Class<? extends Action> actionCls){
		if (actionKey == null){
			throw new IllegalArgumentException("The actionKey can not be null");
		}
		actionKey = actionKey.trim();
		if ("".equals(actionKey))
			throw new IllegalArgumentException("The actionKey can not be blank");
		if (actionCls == null)
			throw new IllegalArgumentException("The controllerClass can not be null");
		if (!actionKey.startsWith("/"))
			actionKey = "/" + actionKey;
		if (routeMap.containsKey(actionKey))
			throw new IllegalArgumentException("The actionKey already exists: " + actionKey);
		
		routeMap.put(actionKey, actionCls);
		
	}
	
	/**
	 * 获取Action路由数据
	 * @return
	 */
	public final Map<String, Class<? extends Action>> getRouteMap(){
		return this.routeMap;
	}
}
