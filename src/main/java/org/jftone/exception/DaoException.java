/**
 * DaoException.java
 * 框架异常对象，主要在Dao层以下捕获
 * 
 * zhoubing
 * 2016-2-24
 */
package org.jftone.exception;


public class DaoException extends Exception {
	
	private static final long serialVersionUID = -1262261495152545945L;

	public DaoException() {
		super();
	}

	public DaoException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public DaoException(String msg) {
		super(msg);
	}

	public DaoException(Throwable cause) {
		super(cause);
	}
}
