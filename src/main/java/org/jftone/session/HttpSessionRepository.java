package org.jftone.session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jftone.component.BeanContext;
import org.jftone.config.PropertyConfigurer;
import org.jftone.data.CacheTemplate;
import org.jftone.data.memcache.MemcacheTemplate;
import org.jftone.data.redis.RedisTemplate;
import org.jftone.exception.ComponentException;
import org.jftone.util.StringUtil;

public final class HttpSessionRepository {
	private Log log = LogFactory.getLog(HttpSessionRepository.class);
	
	public static final String SID_TOKEN = "JFT-AUTH-TOKEN";
	public static String SESSION_ID = "JFTID";
	public static String COOKIE_DOMAIN = "";
	public static String COOKIE_PATH = "/";
	
	private final String MEMCACHE = "memcache";
	private final String REDIS = "redis";

    private CacheTemplate cacheTemplate;

    private HttpSessionRepository() {
    	super();
    }

    static class HttpSessionRepositoryHolder{
    	static HttpSessionRepository INSTANCE = new HttpSessionRepository();
    }
    
    public static HttpSessionRepository getInstance(){
        return HttpSessionRepositoryHolder.INSTANCE;
    }
    
    /**
     * 初始化session共享相关参数
     * @param sessionType
     * @throws ComponentException 
     */
    public void initParam(String sessionType) throws ComponentException{
    	if(sessionType == null || sessionType.equals("")) return;
    	HttpSessionRepository.COOKIE_DOMAIN = PropertyConfigurer.get(PropertyConfigurer.COOKIE_DOMAIN);
    	String timeout = PropertyConfigurer.get(PropertyConfigurer.SESSION_TIMEOUT);
    	if(timeout != null && StringUtil.isNumber(timeout)){
    		CacheHttpSession.TIMEOUT = Integer.parseInt(timeout)*60;
    	}
    	//设置session缓存对象
    	//初始化Session保存对象
    	try {
    		if(sessionType.equals(MEMCACHE)) {
    			cacheTemplate = BeanContext.getBean(MemcacheTemplate.class);
    		}else if(sessionType.equals(REDIS)) {
    			cacheTemplate = BeanContext.getBean(RedisTemplate.class);
    		}
		} catch (ComponentException e) {
			log.error("获取session共享Bean服务接口错误", e);
			throw e;
		}
    }

    /**
     * 新建session对象
     * @param servletContext
     * @return
     */
    HttpSession newSession(ServletContext servletContext) {
        return CacheHttpSession.createNewSession(servletContext, cacheTemplate);
    }

    /**
     * 根据sessionid获取一个session对象
     * @param sessionId
     * @param servletContext
     * @return 
     */
    HttpSession getSession(String sessionId, ServletContext servletContext){
        if (!cacheTemplate.exists(CacheHttpSession.SESSION_PREFIX+sessionId)) {
        	return null;
        } 
        return CacheHttpSession.getExistSession(sessionId, servletContext, cacheTemplate);
    }
}
