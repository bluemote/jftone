/**
 * ActionContext.java
 * 读取Servlet Action配置文件
 * 
 * @author		zhoubing
 * @date   		Mar 29, 2012
 * @revision	v1.0
 */
package org.jftone.action;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jftone.component.ControllerContext;
import org.jftone.config.ActionInterceptor;
import org.jftone.config.AppConfig;
import org.jftone.config.Route;
import org.jftone.exception.ActionException;

final class ActionContext {
	private Logger log = LoggerFactory.getLogger(ActionContext.class);
	private final String LINE = "/";
	
	private Map<String, ActionMapping> actionMapping = new HashMap<String, ActionMapping>();
	
	private List<ActionInterceptor> actionInterceptor = new ArrayList<ActionInterceptor>();

	/**
	 * 获取action配置文件Action
	 * @return actionMap
	 */
	public Map<String, ActionMapping> getActionMapping() {
		return actionMapping;
	}
	
	/**
	 * Action拦截
	 * @return
	 */
	public List<ActionInterceptor> getActionInterceptor() {
		return actionInterceptor;
	}


	/**
	 * 解析Servlet路由配置
	 * 
	 * @param configClass
	 * @return
	 * @throws ActionException
	 */
	public void loadRouteMapping(String configClass) throws ActionException {
		try {
			log.error("开始加载路由映射......");
			Route route = new Route();
			//如果有注解配置的Action，则优先加入路由配置
			Map<String, Class<? extends Action>> routeMaps = ControllerContext.getAll();
			if(!routeMaps.isEmpty()) {
				route.putAll(routeMaps);
			}
			
			// 如果没有配置资源文件，则取默认配置文件
			if (null != configClass && !"".equals(configClass)) {
				
				Class<?> appConfigClazz = Class.forName(configClass);
				if(!AppConfig.class.isAssignableFrom(appConfigClazz)) {
					throw new ActionException("please config AppConfig implement class");
				}
				AppConfig appConfig = (AppConfig)appConfigClazz.newInstance();
				//加载用户自定义配置路由映射
				appConfig.loadRoute(route);
				//拦截器
				actionInterceptor = appConfig.loadInterceptor();
			}
			//创建路由信息
			buildActionMapping(route.getRouteMap());
		} catch (Exception e) {
			log.error("解析配置数据错误"+e.getMessage(), e);
			throw new ActionException("解析配置数据错误"+e.getMessage(), e);
		}
	}
	
	/**
	 * 创建路由信息
	 * @param routeMap
	 */
	private void buildActionMapping(Map<String, Class<? extends Action>> routeMap) {
		String mappingKey = null;
		Set<String> excludedMethod = getExcludedMethod();
		for (Map.Entry<String, Class<? extends Action>> entry : routeMap.entrySet()) {
			Class<? extends Action> actionClazz = entry.getValue();
			String actionKey = entry.getKey();
			/**如果不是静态默认路由，则直接记录action映射数据，方法从method获取
			if (!Const.URL_PARTERN_STATIC.equals(AppConfig.get(AppConfig.URL_PARTERN))){
				if(actionMapping.put(actionKey, new ActionMapping(actionKey, actionClazz, "")) != null ){
					throw new RuntimeException(String.format("Action:%s不能映射，%s已经存", actionClazz.getName(), actionKey));
				}
				continue;	
			}*/
			Method[] methods = actionClazz.getDeclaredMethods();
			for (Method method : methods) {
				StringBuilder mappingBuilder = new StringBuilder(actionKey);
				String methodName = method.getName();
				if (excludedMethod.contains(methodName) || 
						method.getParameterTypes().length != 0 || 
						!Modifier.isPublic(method.getModifiers())){
					continue ;
				}
				if (methodName.equals(ActionHandler.ACTION_METHOD)) {
					methodName = "";
				}else {
					mappingBuilder.append(LINE).append(methodName);
				}
				mappingKey = mappingBuilder.toString();
				ActionMapping actionMap = new ActionMapping(mappingKey, actionClazz, methodName);
				if(actionMapping.put(mappingKey, actionMap) != null ){
					throw new RuntimeException(String.format("Action:%s不能映射，%s已经存", actionClazz.getName(), mappingKey));
				}
				mappingBuilder = null;
			}
			if(!actionMapping.containsKey(actionKey)){
				actionMapping.put(actionKey, new ActionMapping(actionKey, actionClazz, ""));
			}
		}
	}
	
	private Set<String> getExcludedMethod() {
		Set<String> excludedMethod = new HashSet<String>();
		Method[] methods = ActionSupport.class.getMethods();
		for (Method m : methods) {
			if (m.getParameterTypes().length == 0)
				excludedMethod.add(m.getName());
		}
		return excludedMethod;
	}
}
