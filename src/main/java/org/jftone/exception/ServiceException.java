/**
 * ServiceException.java
 * 框架异常对象，主要在ServiceException层捕获
 * 
 * zhoubing
 * 2016-2-24
 */
package org.jftone.exception;


public class ServiceException extends Exception {
	
	private static final long serialVersionUID = 8498869147511639172L;

	public ServiceException() {
		super();
	}

	public ServiceException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public ServiceException(String msg) {
		super(msg);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}
}
