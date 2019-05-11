/**
 * MessageResource.java
 * 资源Delegate对象
 * 
 * @author		zhoubing
 * @date   		Apr 23, 2012
 * @revision	v1.0
 */
package org.jftone.action;

/**
 * @author zhoubing
 *
 */
final class MessageResource {
	private ResourceContext resourceContext;
	
	public MessageResource(ResourceContext resourceContext){
		this.resourceContext = resourceContext;
	}
	
	/**
	 * 取得关键字为key的值
	 * @param key
	 * @return
	 */
	public String getText(String key){
		if(null == resourceContext) return null;
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
		if(null == resourceContext) return null;
		return resourceContext.getText(key, arguments);
	}
	
	/**
	 * 取得关键字为key的数组值
	 * @param key
	 * @return
	 */
	public String[] getTextArray(String key){
		if(null == resourceContext) return null;
		return resourceContext.getTextArray(key);
	}	
}
