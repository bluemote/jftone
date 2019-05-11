/**
 * Action.java
 * Servlet请求接口
 * 
 * @author		zhoubing
 * @date   		Mar 28, 2012
 * @revision	v1.0
 */
package org.jftone.action;

import org.jftone.exception.ActionException;



/**
 * @author zhoubing
 *
 */
public interface Action {
	public void initReqParameter(ActionProxy proxy) throws ActionException;
	/**
	 * 框架执行方法，调用用户自定义Action对象指定方法，不能覆盖
	 * @return
	 * @throws ActionException
	 */
	public void handleRequest() throws ActionException;
}
