package org.jftone.util;

import java.util.List;
import java.util.Map;

import org.jftone.model.Model;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public final class JsonUtil {
	/**
	 * 获取Json Data Map对象串
	 * @param map
	 */
	public static String getJsonStr(Map<String, Object> map){
		return JSONObject.fromObject(map).toString();
	}
	/**
	 * 获取Json Dataset List对象串
	 * @param list
	 */
	public static String getJsonStr(List<?> list){
		return JSONArray.fromObject(list).toString();
	}
	
	/**
	 * 获取Json对象，Object主要为各种Model串
	 * @param bean
	 */
	public static String getJsonStr(Model bean){
		return JSONObject.fromObject(bean).toString();
	}
}
