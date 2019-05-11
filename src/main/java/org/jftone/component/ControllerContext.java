package org.jftone.component;

import java.util.HashMap;
import java.util.Map;

import org.jftone.action.Action;
import org.jftone.component.core.BeanLoaderManager;
import org.jftone.exception.ComponentException;

public final class ControllerContext {
	
	private static Map<String, Class<? extends Action>> controllerMap = new HashMap<String, Class<? extends Action>>();	
	
	public static Map<String, Class<? extends Action>> getAll() {
		return controllerMap;
	}
	
	/**
	 * 判断是否包含存在映射KEY的Action类
	 * @param mappingKey
	 * @return true|false
	 */
	public static boolean containsKey(String mappingKey){
		return controllerMap.containsKey(mappingKey);
	}
	
	
	public static void set(String mappingKey, Class<? extends Action> beanClazz){
		controllerMap.put(mappingKey, beanClazz);
	}
	
	public static int getCount(){
		return controllerMap.size();
	}
	
	public static void clear(){
		controllerMap.clear();
	}
	
	public static Class<? extends Action> get(String mappingKey) {
		return controllerMap.get(mappingKey);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void doSetterBean(Class<? extends Action> actionClazz, Action action) throws ComponentException {
		BeanLoaderManager.doSetterBean((Class)actionClazz, action);
	}
}
