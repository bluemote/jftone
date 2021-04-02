/**
 * MessageResource.java
 * 资源Delegate对象
 * 
 * @author		zhoubing
 * @date   		Apr 23, 2012
 * @revision	v1.0
 */
package org.jftone.action;

import java.util.Locale;

/**
 * @author zhoubing
 *
 */
public final class MessageResource {
	private ResourceContext resourceContext;
	
	public MessageResource(ResourceContext resourceContext){
		this.resourceContext = resourceContext;
	}
	
	public void loadResource(Locale local) {
		this.resourceContext.loadResource(local);
	}
	
	/**
	 * 取得关键字为key的值
	 * @param key
	 * @return
	 */
	public String getText(String key){
		return resourceContext.getText(key);
	}
	/**
	 * 取得关键字为key的值
	 * 值的字符形式为:xxx{0}xxxxxx{1}xxx
	 * arguments按照顺序替换对应{index}
	 * 
	 * @param key
	 * @param arguments
	 * @return
	 */
	public String getText(String key, Object... arguments){
		return resourceContext.getText(key, arguments);
	}
	
	/**
	 * 取得关键字为key的数组值
	 * @param key
	 * @return
	 */
	public String[] getTextArray(String key){
		return resourceContext.getTextArray(key);
	}	
}
