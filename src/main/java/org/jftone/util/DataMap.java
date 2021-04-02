/**
 * DataMap.java
 * 数据对象
 * 
 * @author		zhoubing
 * @date   		Nov 15, 2011
 * @revision	v1.0
 */
package org.jftone.util;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class DataMap<K, V> extends HashMap<K, V> implements IData<K, V> {
	private static final long serialVersionUID = 1L;

	public String[] getKeys() {
		Set<K> set = super.keySet();
		Iterator<K> it = set.iterator();
		String[] keys = new String[set.size()];
		for(int i=0; it.hasNext(); i++){
			keys[i] =  (String)it.next();
		}
		return keys;
	}

	public V get(Object key, Object defaultValue) {
		@SuppressWarnings("unchecked")
		V o =  (V)defaultValue;
		if(!emptyKey(key)){
			o = get(key);
		}
		return o;
	}

	public String getString(String key) {
		return String.valueOf(get(key));
	}
	public String getString(String key, String defaultValue) {
		String s = defaultValue;
		if(!emptyKey(key)){
			s = String.valueOf(get(key));
		}
		return s;
	}	
	
	public String[] getArray(String key) {
		String[] s = null;
		Object obj = get(key);
		if(obj instanceof String){
			s = new String[]{String.valueOf(obj)};
		}else{
			s = (String[])obj;
		}
		return s;
	}
	public String[] getArray(String key, String[] defaultValue) {
		String[] s = defaultValue;
		if(!emptyKey(key)){
			s = getArray(key);
		}
		return s;
	}


	public boolean getBoolean(String key) {
		return Boolean.valueOf(getString(key));
	}
	public boolean getBoolean(String key, boolean defaultValue) {
		boolean flag = defaultValue;
		if(!emptyKey(key)){
			flag = getBoolean(key);
		}
		return flag;
	}

	public char getChar(String key) {
		return (Character)get(key);
	}
	public char getChar(String key, char defaultValue) {
		char c = defaultValue;
		if(!emptyKey(key)){
			c = getChar(key);
		}
		return c;
	}

	public double getDouble(String key) {
		return Double.parseDouble(getString(key));
	}
	public double getDouble(String key, double defaultValue) {
		double d = defaultValue;
		if(!emptyKey(key)){
			d = getDouble(key);
		}
		return d;
	}

	public float getFloat(String key) {
		return Float.parseFloat(getString(key));
	}
	public float getFloat(String key, float defaultValue) {
		float f = defaultValue;
		if(!emptyKey(key)){
			f = getFloat(key);
		}
		return f;
	}
	
	public byte getByte(String key) {
		return Byte.parseByte(getString(key));
	}
	public byte getByte(String key, byte defaultValue) {
		byte b = defaultValue;
		if(!emptyKey(key)){
			b = getByte(key);
		}
		return b;
	}

	public int getInt(String key) {
		return Integer.parseInt(getString(key));
	}
	public int getInt(String key, int defaultValue) {
		int i = defaultValue;
		if(!emptyKey(key)){
			i = getInt(key);
		}
		return i;
	}

	public long getLong(String key) {
		return Long.parseLong(getString(key));
	}
	public long getLong(String key, long defaultValue) {
		long l = defaultValue;
		if(!emptyKey(key)){
			l = getLong(key);
		}
		return l;
	}

	public short getShort(String key) {
		return Short.parseShort(getString(key));
	}
	public short getShort(String key, short defaultValue) {
		short s = defaultValue;
		if(!emptyKey(key)){
			s = getShort(key);
		}
		return s;
	}
	
	/**
	 * 判断DataMap对象中key是否为空
	 * @param key
	 * @return
	 */
	private boolean emptyKey(Object key){
		boolean flag = true;
		if(null != get(key)){
			flag = false;
		}
		return flag;
	}

	@SuppressWarnings("unchecked")
	public IData<K, V> getData(String key) {
		return (IData<K, V>)get(key);
	}

	public Date getDate(String key) throws ParseException {
		return DateUtil.parseDatetime(getString(key));
	}

	public Date getDate(String key, Date defaultValue) throws ParseException {
		if(!emptyKey(key)){
			return DateUtil.parseDatetime(getString(key));
		}
		return defaultValue;	
	}

	public Date getDate(String key, String fmt) throws ParseException {
		return DateUtil.parse(getString(key), fmt);
	}

	public Date getDate(String key, String fmt, Date defaultValue) throws ParseException {
		if(!emptyKey(key)){
			return DateUtil.parse(getString(key), fmt);
		}
		return defaultValue;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder("{");
		String[] keys = this.getKeys();
		int i=0;
		for(String k : keys){
			if(i>0) sb.append(",");
			sb.append(k+"=").append(this.get(k));
			i++;
		}
		sb.append("}");
		return sb.toString();
	}
}
