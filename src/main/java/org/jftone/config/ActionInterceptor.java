package org.jftone.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jftone.action.Action;
import org.jftone.exception.ActionException;

public interface ActionInterceptor {
	
	public boolean before(Action action, String actionMethod, HttpServletRequest request, HttpServletResponse response);
	
	public boolean after(Action action, String actionMethod, HttpServletRequest request, HttpServletResponse response);
	
	public boolean throwable(Action action, String actionMethod, HttpServletRequest request, HttpServletResponse response, ActionException exception);
}
