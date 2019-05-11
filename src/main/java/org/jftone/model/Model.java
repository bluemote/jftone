/**
 * Model.java
 * 规范数据库映射对象
 * 
 * @author		zhoubing
 * @date   		Jul 7, 2011
 * @revision	v1.0
 */
package org.jftone.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.jftone.config.Const;
import org.jftone.exception.CommonException;
import org.jftone.util.ObjectUtil;


public class Model implements Serializable {
	private static final long serialVersionUID = 1L;

	public String toString(){
		StringBuilder sb = new StringBuilder(this.getClass().getName()+"{");
		Field[] fields = this.getClass().getDeclaredFields();
		int i=0;
		for(Field f : fields){
			int modifier = f.getModifiers();
			if(modifier == Modifier.FINAL || modifier == Modifier.STATIC
					|| modifier == 26 ){
				continue;
			}
			if(i>0) sb.append(Const.SPLIT_COMMA);
			try {
				sb.append(f.getName()+"=").append(ObjectUtil.getProperty(this, f.getName()));
			} catch (CommonException e) {
				e.printStackTrace();
			}
			i++;
		}
		sb.append("}");
		return sb.toString();
	}
}
