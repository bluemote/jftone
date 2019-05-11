package org.jftone.data.redis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jftone.annotation.Component;
import org.jftone.config.PropertyConfigurer;
import org.jftone.data.CacheException;
import org.jftone.util.FileUtil;
import org.jftone.util.IData;
import org.jftone.util.StringUtil;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

@Component(init="init", destroy="destroy")
public class RedisService {
	private Log log = LogFactory.getLog(RedisService.class);
	
	private ShardedJedisPool pool = null;
	
	/**
	 * 初始化redis
	 * @throws CacheException
	 */
	@SuppressWarnings("unchecked")
	public void init() throws CacheException{
		String redisConf = PropertyConfigurer.get(PropertyConfigurer.REDIS);
        try {
        	if(StringUtil.isBlank(redisConf)) {
        		return;
        	}
        	IData<String, Object> cacheData = FileUtil.loadClasspathXMLData(redisConf);
    		if(null == cacheData || cacheData.isEmpty()){
    			return;
    		}
    		int maxTotal = cacheData.containsKey("maxTotal")? cacheData.getInt("maxTotal") : 500;
    		int maxIdle = cacheData.containsKey("maxIdle")? cacheData.getInt("maxIdle") : 100;
    		int minIdle = cacheData.containsKey("minIdle")? cacheData.getInt("minIdle") : 10;
    		int maxWait = cacheData.containsKey("maxWait")? cacheData.getInt("maxWait") : 3;
    		List<IData<String, Object>> caches = (List<IData<String, Object>>)cacheData.get("hosts");
    		
        	JedisPoolConfig config =new JedisPoolConfig();	//Jedis池配置
        	config.setMaxTotal(maxTotal);		//最大活动的对象个数
            config.setMaxIdle(maxIdle);			//对象最大空闲时间
            config.setMinIdle(minIdle);
            config.setMaxWaitMillis(1000*maxWait);	//获取对象时最大等待时间
            
            List<JedisShardInfo> jdsInfoList =new ArrayList<>();
            JedisShardInfo jdsShardInfo = null;
            for(IData<String, Object> tmp : caches){
            	jdsShardInfo = new JedisShardInfo(tmp.getString("ip"), tmp.getInt("port"));
            	if(tmp.containsKey("auth")){
            		jdsShardInfo.setPassword(tmp.getString("auth"));
            	}
            	jdsInfoList.add(jdsShardInfo);
    		}
            pool = new ShardedJedisPool(config, jdsInfoList);
		} catch (Exception e) {
			log.error("初始化redis错误", e);
			throw new CacheException("初始化redis错误", e);
		}
	}
	
	/**
	 * 注销关闭redis连接
	 * @throws CacheException
	 */
	public void destroy(){
		if(pool == null) return;
		pool.close();
		pool = null;
	}
	
	/**
	 * 获取ShardedJedisPool连接对象
	 * @return
	 */
	ShardedJedisPool getShardedJedisPool(){
		return this.pool;
	}
}
