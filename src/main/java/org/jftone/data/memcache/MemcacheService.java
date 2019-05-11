package org.jftone.data.memcache;

import java.io.IOException;
import java.net.InetSocketAddress;
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

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;

@Component(init="init", destroy="destroy")
public class MemcacheService {
	private Log log = LogFactory.getLog(MemcacheService.class);
	
	private MemcachedClient client = null;
		
	/**
	 * 初始化memcached配置数据
	 * @throws CacheException 
	 */
	@SuppressWarnings("unchecked")
	public void init() throws CacheException{
		String memcachedConf = PropertyConfigurer.get(PropertyConfigurer.MEMCACHED);
		if(StringUtil.isBlank(memcachedConf)) {
    		return;
    	}
		try {
			IData<String, Object> cacheData = FileUtil.loadClasspathXMLData(memcachedConf);
			if(null == cacheData || cacheData.isEmpty()){
				return;
			}
			int poolSize = cacheData.containsKey("poolSize")? cacheData.getInt("poolSize") : 5;
			long timeout = cacheData.containsKey("timeout")? cacheData.getInt("timeout")*1000 : 5000;
			List<IData<String, Object>> caches = (List<IData<String, Object>>)cacheData.get("hosts");
			List<InetSocketAddress> inetAddrs = new ArrayList<InetSocketAddress>();
			int[] weights = new int[caches.size()];
			int i = 0;
			for(IData<String, Object> tmp : caches){
				inetAddrs.add(new InetSocketAddress(tmp.getString("ip"), tmp.getInt("port")));
				weights[i] = tmp.getInt("weight");
				i++;
			}
			MemcachedClientBuilder builder = new XMemcachedClientBuilder(inetAddrs, weights);
        	builder.setFailureMode(true);
        	builder.setConnectionPoolSize(poolSize); 
        	builder.setOpTimeout(timeout);
			client = builder.build();
		} catch (IOException e) {
			log.error("初始化memcached错误", e);
			throw new CacheException("初始化memcached错误", e);
		}
	}
	
	/**
	 * 注销Memcached连接
	 * @throws IOException
	 */
	public void destroy() throws IOException{
		if(client == null) return;
		client.shutdown();
		client = null;
	}
	
	/**
	 * 获取MemcachedClient
	 * @return
	 */
	MemcachedClient getMemcachedClient(){
		return this.client;
	}
}
