package org.jftone.session;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jftone.data.CacheException;
import org.jftone.data.CacheTemplate;
import org.jftone.util.DateUtil;
import org.jftone.util.StringUtil;

@SuppressWarnings("deprecation")
class CacheHttpSession implements HttpSession {
	private Logger log = LoggerFactory.getLogger(CacheHttpSession.class);
	
    public static int TIMEOUT = 1800;

    public static final String SESSION_PREFIX = "JFTSID_";
    private static final String SESSION_ATTR = "ATTR_";
    private static final String CREATION_TIME = "creationTime";
    private static final String LAST_ACCESSED_TIME = "lastAccessedTime";
    private static final String MAX_INACTIVE_INTERVAL = "maxInactiveInterval";
    
    private String key;
    private String sessionId;
    private boolean newFlag = false;

    private ServletContext servletContext;
    private CacheTemplate cacheService;

    private CacheHttpSession(){
    	super();
    }

    private CacheHttpSession(String sid, ServletContext servletContext, CacheTemplate cacheService) {
        this.servletContext = servletContext;
        this.cacheService = cacheService;
        this.sessionId = StringUtil.isBlank(sid)?java.util.UUID.randomUUID().toString().replace("-", ""):sid;	//创建sessionId
        this.key = SESSION_PREFIX + this.sessionId;
        this.newFlag = true;
		try {
	        cacheService.put(key, newSessionMap(), TIMEOUT);
		} catch (CacheException e) {
			log.error("初始化session错误", e);
		}
    }
    
    /**
     * 创建一个新的session对象并初始化相关参数
     * @param servletContext
     * @param cacheService
     * @return
     */
    public static CacheHttpSession createNewSession(ServletContext servletContext, CacheTemplate cacheService){
    	CacheHttpSession session = new CacheHttpSession(null, servletContext, cacheService);
    	session.setIsNew(true);		//设置为新sessionId
        return session;
    }

    /**
     * 获取存在的session对象
     * @param sessionId
     * @param servletContext
     * @param cacheService
     * @return
     */
    public static CacheHttpSession getExistSession(String sessionId, ServletContext servletContext, CacheTemplate cacheService){
    	CacheHttpSession session = new CacheHttpSession();
    	session.setId(sessionId);
        session.setKey(SESSION_PREFIX + sessionId);
        session.setServletContext(servletContext);
        session.setCacheService(cacheService);
        session.setLastAccessedTime(DateUtil.getMillisecond());		//更新访问时间
        session.setIsNew(false);
        return session;
    }

    public void setId(String id) {
        this.sessionId = id;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setCacheService(CacheTemplate cacheService) {
        this.cacheService = cacheService;
    }
    
    private Map<String, Object> newSessionMap(){
    	long creationTime = DateUtil.getMillisecond();
    	Map<String, Object> map = new HashMap<String, Object>();
		map.put(CREATION_TIME, creationTime);
		map.put(LAST_ACCESSED_TIME, creationTime);
		map.put(MAX_INACTIVE_INTERVAL, TIMEOUT);
		return map;
    }
    
    private Map<String, Object> getSessionMap() throws CacheException {
    	Map<String, Object> map = cacheService.get(key);
    	if(map == null || map.isEmpty()){
    		map = newSessionMap();
    	}
    	return map;
    }
    /**
     * update expire time
     * @throws CacheException 
     */
    public void refresh() throws CacheException{
    	cacheService.expire(key, getMaxInactiveInterval());
    }

    public void setLastAccessedTime(long lastAccessedTime) {
    	Map<String, Object> data;
		try {
			data = getSessionMap();
			data.put(LAST_ACCESSED_TIME, lastAccessedTime);
	    	cacheService.put(key, data, getMaxInactiveInterval());
		} catch (CacheException e) {
			log.error("保存session最新访问时间错误", e);
		}
    }

    public void setKey(String key) {
        this.key = key;
    }
    @Override
    public String getId() {
        return this.sessionId;
    }

    @Override
    public long getCreationTime() {
    	long createTime = 0;
    	try {
    		createTime = (long) getSessionMap().get(CREATION_TIME);
		} catch (CacheException e) {
			log.error("获取session创建时间错误", e);
		}
		return createTime;
    }
    
    @Override
    public long getLastAccessedTime() {
        long lastAccessedTime = 0;
    	try {
    		lastAccessedTime = (long) getSessionMap().get(LAST_ACCESSED_TIME);
		} catch (CacheException e) {
			log.error("获取session最近访问时间错误", e);
		}
		return lastAccessedTime;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
    	TIMEOUT = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return TIMEOUT;
    }

	@Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
    	Object attrObj = null;
		try {
			Map<String, Object> data = getSessionMap();
			attrObj = data.get(SESSION_ATTR+name);
		} catch (CacheException e) {
			log.error("获取session属性值错误", e);
		}
		return attrObj;
    }

    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(getAttributeKeys());
    }

    private Set<String> getAttributeKeys(){
    	Set<String> attrNames = new HashSet<>();
		try {
			Map<String, Object> data = getSessionMap();
			Set<String> keys = data.keySet();
	        for (String key : keys){
	            if (key.startsWith(SESSION_ATTR)){
	                attrNames.add(key.substring(SESSION_ATTR.length()));
	            }
	        }
		} catch (CacheException e) {
			log.error("获取session属性键值错误", e);
		}
        return attrNames;
    }

    private boolean setIsNew(boolean newFlag) {
        return this.newFlag = newFlag;
    }

    @Override
    public String[] getValueNames() {
        return getAttributeKeys().toArray(new String[0]);
    }

    @Override
    public void setAttribute(String name, Object value) {
    	try {
			Map<String, Object> data = getSessionMap();
			data.put(SESSION_ATTR + name, value);
			cacheService.put(key, data, getMaxInactiveInterval());
		} catch (CacheException e) {
			log.error("设置session属性值错误", e);
		}
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
    	try {
			Map<String, Object> data = getSessionMap();
			data.remove(SESSION_ATTR + name);
			cacheService.put(key, data, getMaxInactiveInterval());
		} catch (CacheException e) {
			log.error("清除session属性错误", e);
		}
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        try {
			cacheService.delete(key);
		} catch (CacheException e) {
			log.error("清除session错误", e);
		}
    }

    @Override
    public boolean isNew() {
        return newFlag;
    }
}
