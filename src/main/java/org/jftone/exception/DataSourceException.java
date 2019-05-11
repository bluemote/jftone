/**
 * DataSourceException.java
 * 框架异常对象，主要在Dao层以下捕获
 * 
 * zhoubing
 * 2016-2-24
 */
package org.jftone.exception;


public class DataSourceException extends Exception {

	private static final long serialVersionUID = -2393939316984522798L;

	public DataSourceException() {
		super();
	}

	public DataSourceException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public DataSourceException(String msg) {
		super(msg);
	}

	public DataSourceException(Throwable cause) {
		super(cause);
	}
}
