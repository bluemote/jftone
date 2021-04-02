/**
 * Data.java
 * 数据对象接口，继承Map接口
 * 
 * @author		zhoubing
 * @date   		Nov 15, 2011
 * @revision	v1.0
 */
package org.jftone.util;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

public abstract interface IData<K, V> extends Map<K, V> , Serializable{
	
	public abstract V get(Object key, Object defaultValue);
	
	public abstract String[] getKeys();
	
	public abstract IData<K, V> getData(String key);
	
	public abstract String getString(String key);
	public abstract String getString(String key, String defaultValue);
	
	public abstract String[] getArray(String key);
	public abstract String[] getArray(String key, String[] defaultValue);
	
	public abstract int getInt(String key);
	public abstract int getInt(String key,  int defaultValue);

	public abstract float getFloat(String key);
	public abstract float getFloat(String key, float defaultValue);

	public abstract double getDouble(String key);
	public abstract double getDouble(String key, double defaultValue);

	public abstract long getLong(String key);
	public abstract long getLong(String key, long defaultValue);

	public abstract short getShort(String key);
	public abstract short getShort(String key, short defaultValue);

	public abstract byte getByte(String key);
	public abstract byte getByte(String key, byte defaultValue);

	public abstract char getChar(String key);
	public abstract char getChar(String key, char defaultValue);

	public abstract boolean getBoolean(String key);
	public abstract boolean getBoolean(String key, boolean defaultValue);
	
	public abstract Date getDate(String key) throws ParseException;
	public abstract Date getDate(String key, Date defaultValue)throws ParseException;
	
	public abstract Date getDate(String key, String fmt)throws ParseException;
	public abstract Date getDate(String key, String fmt, Date defaultValue)throws ParseException;
	
}
