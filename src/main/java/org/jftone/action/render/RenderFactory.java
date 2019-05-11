package org.jftone.action.render;

import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;


public class RenderFactory {
	
	private static RenderFactory renderFactory;
	
	private RenderFactory() {
		
	}
	
	public static RenderFactory getInstance() {
		return renderFactory;
	}
	
	public static void init(ServletContext servletContext) {
		renderFactory = new RenderFactory();
		FreeMarkerRender.init(servletContext, Locale.getDefault());
	} 
	
	public Render getRender(String view, Map<String, Object> map) {
		return new FreeMarkerRender(view, map);
	}
}


