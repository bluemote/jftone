package org.jftone.data.redis;

import java.util.Collection;

import org.jftone.annotation.Component;
import org.jftone.annotation.Resource;
import org.jftone.data.CacheException;
import org.jftone.data.CacheTemplate;
import org.jftone.util.KryoSerializer;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Component
public class RedisTemplate implements CacheTemplate {
	private ShardedJedisPool pool;
	
	@Resource
	private RedisService redisService;
	
	public void setRedisService(RedisService redisService){
		this.redisService = redisService;
		if(null != redisService) {
			this.pool = redisService.getShardedJedisPool();
		}
	}
	
	/**
	 * 暴露ShardedJedis对象给外部
	 * 定制特需接口及API需要
	 * 例如事务支持，管道等调用方式
	 * @return
	 */
	public ShardedJedis getShardedJedis(){
		return this.pool.getResource();
	}
	public void close(ShardedJedis jedis) {  
        if (jedis == null) {
        	return;  
        }
        jedis.close();
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
		boolean result = false;
		if(null == key || null == value){
			return result;
		}
		ShardedJedis shardedJedis = getShardedJedis();
        try {
        	byte[] k = KryoSerializer.writeObject(key);
        	byte[] v = KryoSerializer.writeObject(value);
        	byte[] nx = KryoSerializer.writeObject("NX");
        	byte[] ex = KryoSerializer.writeObject("EX");
        	if(second > LONG_TIME){
        		/**
        	     * 存储数据到缓存中，并制定过期时间和当Key存在时是否覆盖。
        	     * @param nxxx nxxx的值只能取NX或者XX，如果取NX，则只有当key不存在是才进行set，如果取XX，则只有当key已经存在时才进行set
        	     * @param expx expx的值只能取EX或者PX，代表数据过期时间的单位，EX代表秒，PX代表毫秒。
        	     * @param time 过期时间，单位是expx所代表的单位。
        	     * set(String key, String value, String nxxx, String expx, long time);
        	     */
        		String ret = shardedJedis.set(k, v, nx, ex, second);
        		result = null != ret && ret.equals("OK")? true : false;
        	}else{
        		result = shardedJedis.setnx(k, v)>0? true : false;
        	}
		} catch (Exception e) {
			throw new CacheException("新增["+key+"]错误", e);
		}finally{
			close(shardedJedis);
		}
        return result;
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
		if(null == key || null == value){
			return;
		}
		ShardedJedis shardedJedis = getShardedJedis();
        try {
        	byte[] k = KryoSerializer.writeObject(key);
        	byte[] v = KryoSerializer.writeObject(value);
        	if(shardedJedis.exists(k)){
        		shardedJedis.del(k);
        	}
        	if(second > LONG_TIME){
        		shardedJedis.setex(k, second, v);
        	}else{
        		shardedJedis.set(k, v);
        	}
		} catch (Exception e) {
			throw new CacheException("设置["+key+"]错误", e);
		} finally{
			close(shardedJedis);
		}
    } 
    
    /**
     * 删除对应KEY
     * @param key
     * @throws CacheException
     */
	@Override
    public void delete(String key) throws CacheException{
		if(null == key){
			return;
		}
		ShardedJedis shardedJedis = getShardedJedis();
    	try {
    		byte[] k = KryoSerializer.writeObject(key);
    		shardedJedis.del(k);
		} catch (Exception e) {
			throw new CacheException("删除["+key+"]数据错误", e);
		} finally{
			close(shardedJedis);
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
		throw new CacheException("Redis接口调用，返回Key对象，必须指定返回数据类型的Class");
	}
    public <T> T get(String key, Class<T> clazz) throws CacheException {  
    	if(null == key){
			return null;
		}
		ShardedJedis shardedJedis = getShardedJedis();
        try {
        	byte[] k = KryoSerializer.writeObject(key);
        	byte[] v = shardedJedis.get(k);
        	if(v == null || v.length <=0){
        		return null;
        	}
			return KryoSerializer.readObject(v, clazz);
		} catch (Exception e) {
			throw new CacheException("获取["+key+"]数据错误", e);
		} finally{
			close(shardedJedis);
		}
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
		if(null == key){
			return false;
		}
		ShardedJedis shardedJedis = getShardedJedis();
        try {
        	byte[] k = KryoSerializer.writeObject(key);
			return shardedJedis.expire(k, second)>0? true : false;
		} catch (Exception e) {
			throw new CacheException("设置["+key+"]过期时间错误", e);
		} finally{
			close(shardedJedis);
		}
    }
	
	@Override
	public boolean exists(String key){
		boolean flag = false;
		if(null == key){
			return flag;
		}
		ShardedJedis shardedJedis = getShardedJedis();
		try {
			byte[] k = KryoSerializer.writeObject(key);
			flag = shardedJedis.exists(k);
		} catch (Exception e) {
			flag = false;
		} finally{
			close(shardedJedis);
		}
		return flag;
	}
	
	/**
	 * 关闭Redis客户端
	 */
	@Override
    public void shutdown() throws CacheException{
		ShardedJedis shardedJedis = getShardedJedis();
    	try {
    		close(shardedJedis);
    		if(pool != null){
    			pool.close();
    			pool.destroy();
    		}
    		pool = null;
        }catch (Exception e) {
        	throw new CacheException("关闭Redis客户端错误", e);
        }
    }
    
	@Override
	public void flush() throws CacheException {
		ShardedJedis shardedJedis = getShardedJedis();
		try {
			Collection<Jedis>  collection = shardedJedis.getAllShards();
			for(Jedis jedis : collection){
				jedis.flushAll();
			}
		} catch (Exception e) {
			throw new CacheException("清空Redis数据错误", e);
		}finally{
			close(shardedJedis);
		}
		
	}

}
