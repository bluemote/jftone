package org.jftone.data.memcache;

import org.jftone.annotation.Component;
import org.jftone.annotation.Resource;
import org.jftone.data.CacheException;
import org.jftone.data.CacheTemplate;

import net.rubyeye.xmemcached.MemcachedClient;

@Component
public class MemcacheTemplate implements CacheTemplate {
	private MemcachedClient client = null;
	
	@Resource
	private MemcacheService memcacheService;
	
	public void setMemcacheService(MemcacheService memcacheService){
		this.memcacheService = memcacheService;
		if(null != memcacheService) {
			this.client = memcacheService.getMemcachedClient();
		}
	}
	
	/**
	 * 暴露Memcache客户端对象给外部
	 * 定制特需接口需要，需要熟悉memcache客户端调用API
	 * @return
	 */
	public MemcachedClient getMemcachedClient(){
		return this.client;
	}
	
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
        try {
        	return client.add(key, second, value);
		} catch (Exception e) {
			throw new CacheException("新增["+key+"]错误", e);
		} 
    } 
    
    /**
     * 设置一个key数据，如果存在则替换
     * @param key
     * @param value
     * @return
     * @throws CacheException
     */
	@Override
    public void put(String key, Object value) throws CacheException { 
        put(key, value, LONG_TIME);
    }
	@Override
    public void put(String key, Object value, int second) throws CacheException {  
        try {
        	client.setWithNoReply(key, second, value);
		} catch (Exception e) {
			throw new CacheException("设置["+key+"]错误", e);
		} 
    } 
    
    /**
     * 删除对应KEY
     * @param key
     * @throws CacheException
     */
	@Override
    public void delete(String key) throws CacheException{ 
    	try {
			client.deleteWithNoReply(key);
		} catch (Exception e) {
			throw new CacheException("删除["+key+"]数据错误", e);
		} 
    }   
      
    /**
     * 获取key对应的数据
     * @param key
     * @return
     * @throws CacheException
     */
	@Override
    public <T> T get(String key) throws CacheException {  
        try {
			return client.get(key);
		} catch (Exception e) {
			throw new CacheException("获取["+key+"]数据错误", e);
		} 
    }
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		return get(key);
	}
    
    /**
     * 设置key过期时间
     * @param key
     * @param second
     * @return
     * @throws CacheException
     */
	@Override
    public boolean expire(String key, int second) throws CacheException {  
        try {
			return client.touch(key, second);
		} catch (Exception e) {
			throw new CacheException("设置["+key+"]过期时间错误", e);
		} 
    }
	
	/**
	 * 关闭memcache客户端
	 */
	@Override
    public void shutdown() throws CacheException{
    	try {
    		if(null != client){
    			client.shutdown();
    		}
        	client = null;
        }catch (Exception e) {
        	throw new CacheException("关闭memcache客户端错误", e);
        }
    }
    
	@Override
	public void flush() throws CacheException {
		try {
			client.flushAll();
		} catch (Exception e) {
			throw new CacheException("清空memcache数据错误", e);
		}
		
	}

	@Override
	public boolean exists(String key){
		boolean flag = false;
		try {
			flag = client.get(key)!= null;
		} catch (Exception e) {
			flag = false;
		} 
		return flag;
	}
	
	/**
     * 替换key，如果存在则报错
     * @param key
     * @param value
     * @throws CacheException
     */
	public void replace(String key, Object value) throws CacheException {  
        replace(key, value, LONG_TIME);  
    }  
	public void replace(String key, Object value, int second) throws CacheException {  
        try {
			client.replaceWithNoReply(key, second, value);
		} catch (Exception e) {
			throw new CacheException("更新["+key+"]数据错误", e);
		} 
    }  
}
