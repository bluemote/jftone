/**
 * DaoException.java
 * 框架异常对象，主要在Dao层以下捕获
 * 
 * zhoubing
 * 2016-2-24
 */
package org.jftone.exception;


public class DbException extends Exception {

	private static final long serialVersionUID = -2393939316984522798L;

	public DbException() {
		super();
	}

	public DbException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public DbException(String msg) {
		super(msg);
	}

	public DbException(Throwable cause) {
		super(cause);
	}
}
