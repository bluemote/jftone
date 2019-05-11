/**
 * ServiceException.java
 * 框架异常对象，主要在ServiceException层捕获
 * 
 * zhoubing
 * 2016-2-24
 */
package org.jftone.exception;


public class ComponentException extends Exception {

	private static final long serialVersionUID = 3417438326419536141L;

	public ComponentException() {
		super();
	}

	public ComponentException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public ComponentException(String msg) {
		super(msg);
	}

	public ComponentException(Throwable cause) {
		super(cause);
	}
}
