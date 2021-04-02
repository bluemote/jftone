package org.jftone.data.memory;

import java.util.HashMap;
import java.util.Map;

import org.jftone.annotation.Component;
import org.jftone.data.CacheException;
import org.jftone.data.CacheTemplate;

@Component
public class MemoryTemplate implements CacheTemplate {
	private static Map<Object, Object> JFTMAP = new HashMap<>();
	
	/**
	 * 新增一个缓存key数据，如果key存在则报错
	 * @param key
	 * @param value
	 * @return
	 * @throws CacheException
	 */
	@Override
	public boolean add(String key, Object value) throws CacheException { 
		return add(key, value, LONG_TIME);
    }  
	@Override
    public boolean add(String key, Object value, int second) throws CacheException {
		if(JFTMAP.containsKey(key)){
			return false;
		}
		JFTMAP.put(key, value);
		return true;
    } 
    
    /**
     * 设置一个key数据，如果存在则替换
     * @param key
     * @param value
     * @return
     * @throws CacheException
     */
	@Override
    public void put(String key, Object value) { 
        put(key, value, -1);
    }
	@Override
    public void put(String key, Object value, int second){  
		JFTMAP.put(key, value);
    } 
    
    /**
     * 删除对应KEY
     * @param key
     * @throws CacheException
     */
	@Override
    public void delete(String key){
		JFTMAP.remove(key);
    }  
      
    /**
     * 获取key对应的数据
     * @param key
     * @return
     * @throws CacheException
     */
	@Override
	@SuppressWarnings("unchecked")
    public <T> T get(String key) throws CacheException {  
		return (T)JFTMAP.get(key);
    }
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		return get(key);
	}
    
    /**
     * 设置key过期时间, 对于内存无效
     * @param key
     * @param second
     * @return
     * @throws CacheException
     */
	@Override
    public boolean expire(String key, int second) throws CacheException {
		JFTMAP.remove(key);
		return true;
    }
	
	/**
	 * 关闭并情况对象
	 */
	@Override
    public void shutdown() throws CacheException{
    	try {
    		if(JFTMAP != null && !JFTMAP.isEmpty()){
    			JFTMAP.clear();
    		}
    		JFTMAP = null;
        }catch (Exception e) {
        	throw new CacheException("关闭Redis客户端错误", e);
        }
    }
    
	@Override
	public void flush() throws CacheException {
		JFTMAP.clear();
	}

	@Override
	public boolean exists(String key){
		return JFTMAP.containsKey(key);
	}
}
