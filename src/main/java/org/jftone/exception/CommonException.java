/**
 * CommonException.java
 * 框架异常对象
 * 
 * zhoubing
 * Jul 5, 2011
 */
package org.jftone.exception;


public class CommonException extends Exception {

	private static final long serialVersionUID = 2149736422436593163L;

	public CommonException() {
		super();
	}

	public CommonException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public CommonException(String msg) {
		super(msg);
	}

	public CommonException(Throwable cause) {
		super(cause);
	}

}
