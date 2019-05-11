package org.jftone.data;

public interface CacheTemplate {
	public static int LONG_TIME = 0;
	/**
	 * 新增一个缓存key数据，如果key存在则报错
	 * @param key
	 * @param value
	 * @return
	 * @throws CacheException
	 */
	public boolean add(String key, Object value) throws CacheException;  
    public boolean add(String key, Object value, int second) throws CacheException;
    
    /**
     * 设置一个key数据，如果存在则替换
     * @param key
     * @param value
     * @return
     * @throws CacheException
     */
    public void put(String key, Object value) throws CacheException;
    public void put(String key, Object value, int second) throws CacheException; 
    
    /**
     * 删除对应KEY
     * @param key
     * @throws CacheException
     */
    public void delete(String key) throws CacheException;  
      
    /**
     * 获取key对应的数据
     * @param key
     * @return
     * @throws CacheException
     */
    public <T> T get(String key) throws CacheException;
    public <T> T get(String key, Class<T> clazz) throws CacheException;
    
    public boolean exists(String key);
    
    /**
     * 设置key过期时间
     * @param key
     * @param second
     * @return
     * @throws CacheException
     */
    public boolean expire(String key, int second) throws CacheException;
	
    public void shutdown() throws CacheException;
    
    public void flush() throws CacheException;
}
