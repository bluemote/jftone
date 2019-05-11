package org.jftone.data;

public class CacheException extends Exception {
	private static final long serialVersionUID = 1544190960789218783L;

	public CacheException() {
		super();
	}

	public CacheException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public CacheException(String msg) {
		super(msg);
	}

	public CacheException(Throwable cause) {
		super(cause);
	}
}
