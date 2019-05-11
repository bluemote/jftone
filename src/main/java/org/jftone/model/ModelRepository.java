/**
 * ModelRepository.java
 * ReflectASM反射Class存储
 * 
 * @author		zhoubing
 * @date   		Jan 3, 2018
 * @revision	v1.0
 */
package org.jftone.model;

import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.reflectasm.MethodAccess;


public class ModelRepository<T extends Model> {
	private static Map<Class<?>, MethodAccess> modelAccessMap = new HashMap<Class<?>, MethodAccess>();
	
	public static MethodAccess get(Class<?> modelClazz){
		MethodAccess modelAccess = modelAccessMap.get(modelClazz);
		if(null == modelAccess) {
			modelAccess = MethodAccess.get(modelClazz);
			modelAccessMap.put(modelClazz, modelAccess);
		}
		return modelAccess;
	}
}
