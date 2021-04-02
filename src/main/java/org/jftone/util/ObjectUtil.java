/**
 * ObjectUtil.java
 *
 * @author    zhoubing
 * @date      Jul 5, 2011
 * @revision  1.0
 */
package org.jftone.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.jftone.exception.CommonException;

public final class ObjectUtil {
	
	/**
	 * POJO对象给POJO对象赋值
	 * @param fromObj	原始对象
	 * @param destObj	目标对象
	 * @throws CommonException 
	 */
    public static void copyObj(Object fromObj ,Object destObj) throws CommonException {
    	copyObj(fromObj , destObj, null);
    }
    
    /**
	 * POJO对象给POJO对象赋值
	 * @param fromObj			原始对象
	 * @param destObj			目标对象
     * @param filterPropertys	不需要赋值的关键字属性集合
     * @throws CommonException 
     */
    public static void copyObj(Object fromObj ,Object destObj,List<String> filterPropertys) throws CommonException {
    	boolean filterFlag = false;
    	Field[] fieldArray = destObj.getClass().getDeclaredFields();
        try {
        	if(fieldArray != null){
        		int modifier = 0;
        		//String attributeType = null;	//属性类型:Integer,String,Date,Class etc
        		String fieldName = null;
        		Object value = null;
        		for(Field field : fieldArray){
        			modifier = field.getModifiers();
        			fieldName = field.getName();
        			//attributeType = field.getType().getSimpleName();
        			//过滤final和static修饰的属性赋值
        			if(modifier == Modifier.FINAL || modifier == Modifier.STATIC || modifier == 26){
        				continue;
        			}
        			//如果有需要过滤的赋值对象则跳过赋值操作
        			if(filterPropertys != null && filterPropertys.size()>0 
        					&& filterPropertys.contains(fieldName)){
            			filterFlag = true;
            		}
        			if(!filterFlag){
        				value = ObjectUtil.getProperty(fromObj, fieldName);
            			if(value != null){
            				ObjectUtil.setProperty(destObj, fieldName, value);
            			}
        			}
        		}
        	}
        } catch (Exception ex) {
        	throw new CommonException(ex);
        }
    }
    
    /**
     * 获取Javabean对象某个属性的值,主要获取getter
     * @param bean	实例化的某个对象
     * @param name	对象的属性名
     * @return	Object
     * @throws ApplicationException
     */
    public static Object getProperty(Object bean ,String name) throws CommonException {
    	Object propertyValue = null;
    	try {
    		if(bean == null){
    			throw new CommonException();
    		}
    		if(name != null && !name.equals("")){
    			String subfix = name.length() == 1?"":name.substring(1);
    			String methodName = "get"+name.substring(0, 1).toUpperCase() + subfix;
            	Method method = bean.getClass().getMethod(methodName, new Class[]{});
            	propertyValue = method.invoke(bean, new Object[]{});
    		}
    		if(propertyValue instanceof java.sql.Timestamp) {
    			Timestamp time = (java.sql.Timestamp)propertyValue;
    			propertyValue = new Date(time.getTime());
    		}else if(propertyValue instanceof java.sql.Time) {
    			java.sql.Time time = (java.sql.Time)propertyValue;
    			propertyValue = new Date(time.getTime());
    		}else if(propertyValue instanceof java.sql.Date) {
    			java.sql.Date time = (java.sql.Date)propertyValue;
    			propertyValue = new Date(time.getTime());
    		}
        } catch (Exception ex) {
        	throw new CommonException(ex);
        }
        return propertyValue;
    }
    
    /**
     * 给指定的Javabean对象的某个属性赋值,主要为setter
     * @param bean	实例化的对象
     * @param name	对象属性名
     * @param value	参数值
     * @throws CommonException
     */
	public static void setProperty(Object bean ,String name, Object value) throws CommonException {
    	Class<? extends Object> cls = bean.getClass();
    	try {
    		if(name == null || name.equals("")){
        		throw new CommonException();
    		}
        	String subfix = name.length() == 1?"":name.substring(1);
			String methodName = "set"+name.substring(0, 1).toUpperCase() + subfix;
			
			Field field = cls.getDeclaredField(name);	//获取属性对象，取得其数据类型
        	Method method = bean.getClass().getMethod(methodName, new Class[]{field.getType()});
        	method.invoke(bean, new Object[]{value});
        } catch (Exception ex) {
        	ex.printStackTrace();
        	throw new CommonException(ex);
        }
    }
    
    /**
     * Javabean对象方法反射调用
     * @param bean			实例化对象
     * @param methodName	方法名
     * @param value			一般对象，如果方法有多个参数，请按照参数顺序装载到List对象中
     * @return
     * @throws CommonException
     */
	public static Object invokeProperty(Object bean ,String methodName,Object... value) throws CommonException {
    	Object propertyValue = null;
    	Class<?>[] paramClass = null;
    	try {
    		if(bean == null || methodName == null || value == null){
    			throw new CommonException();
    		}
    		
    		if(value != null){
    			paramClass = new Class[value.length];
    			int i = 0;
    			for(Object o : value){
    				paramClass[i++] = o.getClass();
        		}
    		}
    		Method method = bean.getClass().getMethod(methodName, paramClass);
        	propertyValue = method.invoke(bean, value);
        	
        } catch (Exception ex) {
        	throw new CommonException(ex);
        }
        return propertyValue;
    }
    
    /**
     * 将字符串按照"_"字符拆分，组装成以头字母为大写的新字符串
     * 如tbl_admin_table =》 TblAdminTable
     * 
     * @param tableName
     * @return
     * @throws CommonException 
     */
    public static String getClassName(String tableName) throws CommonException {
    	String className = "";
    	try {
    		if(null == tableName || "".equals(tableName)){
    			throw new CommonException();
    		}
    		tableName = tableName.toLowerCase();
        	if (tableName.indexOf("_") != -1) {
                String[] temps = tableName.split("_");
                for (int i = 0; i < temps.length; i++) {
                    String s = temps[i];
                    className += s.substring(0, 1).toUpperCase()+ s.substring(1);
                }
            } else {
            	className = tableName.substring(0, 1).toUpperCase() + tableName.substring(1);
            }

        } catch (Exception e) {
        	throw new CommonException(e);
        }
        return className;
    }
    
    /**
     * 将字符串按照"_"字符拆分，组装成以头字母为小写的新字符串
     * 如admin_table =》 adminTable
     * 
     * @param fieldName
     * @return
     * @throws CommonException 
     */
    public static String getPropertyName(String fieldName) throws CommonException {
    	String propertyName = "";
    	propertyName = getClassName(fieldName);
    	propertyName = (propertyName.substring(0, 1)).toLowerCase() + propertyName.substring(1);
        return propertyName;
    }
    
    public static String getSetter(String propertyName) {
    	String setterMethod = "set"+propertyName.substring(0, 1).toUpperCase();
    	if(propertyName.length()>1){
    		setterMethod += propertyName.substring(1);
    	}
    	return setterMethod; 
    }
    
    public static String getGetter(String propertyName) {
    	String getterMethod = "set"+propertyName.substring(0, 1).toUpperCase();
    	if(propertyName.length()>1){
    		getterMethod += propertyName.substring(1);
    	}
    	return getterMethod; 
    }
    
}
