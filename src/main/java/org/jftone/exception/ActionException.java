/**
 * ActionException.java
 * 框架异常对象
 * 
 * zhoubing
 * Jul 5, 2011
 */
package org.jftone.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ActionException extends Exception {

	private static final long serialVersionUID = -6391548901079586880L;

	public ActionException() {
		super();
	}

	public ActionException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public ActionException(String msg) {
		super(msg);
	}

	public ActionException(Throwable cause) {
		super(cause);
	}

	public String getCurStackTrace() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try {
			this.printStackTrace(pw);
		} finally {
			pw.close();
		}
		return sw.toString();
	}
}
