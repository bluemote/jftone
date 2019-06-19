/**
 * ResourceContext.java
 * 加载国际化资源文件
 * 
 * @author		zhoubing
 * @date   		Mar 30, 2012
 * @revision	v1.0
 */
package org.jftone.action;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jftone.config.Const;

/**
 * @author zhoubing
 *
 */
final class ResourceContext {
	
	private Logger log = LoggerFactory.getLogger(ResourceContext.class);
	//本地化对象
	private Locale local = Locale.getDefault();
	//存放本地资源束
	private List<ResourceBundle> resBundles = new ArrayList<ResourceBundle>();
	
	public void setLocal(Locale local) {
		this.local = local;
	}
	/**
	 * 根据配置文件中的资源束字符串加载资源束
	 * @param configResourceStr
	 */
	public void loadResource(String configResourceStr){
		String[] resources = null;
		if(null == configResourceStr || "".equals(configResourceStr)){
			return;
		}
		log.error("开始加载国际化配置......");
		if(configResourceStr.indexOf(Const.SPLIT_COMMA)>-1){
			resources = configResourceStr.split(Const.SPLIT_COMMA);
		}else{
			resources = new String[]{configResourceStr};
		}
		ClassLoader classLoader = this.getClass().getClassLoader();
		for(String resourceName : resources){
			if(resourceName.endsWith(".properties")) resourceName = resourceName.substring(0, resourceName.length()-11);
			resBundles.add(PropertyResourceBundle.getBundle(resourceName, local, classLoader));
		}
	}
	
	/**
	 * 取得关键字为key的值
	 * @param key
	 * @return
	 */
	public String getText(String key){
		return getMsg(key);
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
		String text = getMsg(key);
		return MessageFormat.format(text, arguments);
	}
	
	/**
	 * 取得关键字为key的数组值
	 * @param key
	 * @return
	 */
	public String[] getTextArray(String key){
		String[] textArray = new String[]{};
		if(null == key || "".equals(key) || resBundles.size()==0){
			return textArray;
		}
		for(ResourceBundle resBundle : resBundles){
			if(resBundle.containsKey(key)){
				textArray = resBundle.getStringArray(key);
				break;
			}
		}
		return textArray;
	}
	
	/**
	 * 从资源束中获取key的值
	 * @param key
	 * @return
	 */
	private String getMsg(String key){
		String msg = "";
		if(null == key || "".equals(key) || resBundles.size()==0){
			return msg;
		}
		for(ResourceBundle resBundle : resBundles){
			if(resBundle.containsKey(key)){
				msg = resBundle.getString(key);
				break;
			}
		}
		return msg;
	}
	
}
