package org.jftone.quickstart.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jftone.action.Action;
import org.jftone.config.ActionInterceptor;
import org.jftone.exception.ActionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseInterceptor implements ActionInterceptor {
	private Logger log = LoggerFactory.getLogger(BaseInterceptor.class);

	@Override
	public boolean before(Action action, String methodName, HttpServletRequest request, HttpServletResponse response) {
		
		return true;
	}

	@Override
	public boolean after(Action action, String methodName, HttpServletRequest request, HttpServletResponse responset) {
		
		return true;
	}

	@Override
	public boolean throwable(Action action, String methodName, HttpServletRequest request, HttpServletResponse response,
			ActionException ex) {
		
		return true;
	}
}
