package org.jftone.action.render;

import java.io.PrintWriter;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.jftone.config.Const;
import org.jftone.config.PropertyConfigurer;
import org.jftone.exception.ActionException;

import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * FreeMarkerRender.
 */
public class FreeMarkerRender extends Render {
	
	private static final String contentType = "text/html; charset=" + Const.CHARSET_UTF8;
	private static final Configuration config = new Configuration(Configuration.VERSION_2_3_23);
	
	public FreeMarkerRender(String view, Map<String, Object> map) {
		this.view = view;
		this.map = map;
	}
	
	public static void init(ServletContext servletContext, Locale locale) {
        config.setServletContextForTemplateLoading(servletContext, PropertyConfigurer.get(PropertyConfigurer.TEMPLET_ROOT));
        
        if (PropertyConfigurer.get(PropertyConfigurer.PRODUCT_MODE).equals("false")) {
        	config.setTemplateUpdateDelayMilliseconds(0);
       	}
        else {
        	config.setTemplateUpdateDelayMilliseconds(Const.DEFAULT_FREEMARKER_TEMPLATE_UPDATE_DELAY*1000);
        }
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        //config.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
        config.setObjectWrapper(new BeansWrapperBuilder(Configuration.VERSION_2_3_23).build());
        
        config.setDefaultEncoding(Const.CHARSET_UTF8);
        config.setOutputEncoding(Const.CHARSET_UTF8);
        config.setLocale(locale);
        config.setLocalizedLookup(false);
        config.setNumberFormat("#0.#####");
        config.setDateFormat("yyyy-MM-dd");
        config.setTimeFormat("HH:mm:ss");
        config.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
    }
	

	public static Configuration getConfiguration() {
		return config;
	}
	
	public static void setProperty(String propertyName, String propertyValue) {
		try {
			FreeMarkerRender.getConfiguration().setSetting(propertyName, propertyValue);
		} catch (TemplateException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void setProperties(Properties properties) {
		try {
			FreeMarkerRender.getConfiguration().setSettings(properties);
		} catch (TemplateException e) {
			throw new RuntimeException(e);
		}
	}
    

    public String getContentType() {
    	return contentType;
    }
    
	public void render() throws ActionException {
		response.setContentType(getContentType());
		PrintWriter writer = null;
        try {
			Template template = config.getTemplate(view);
			writer = response.getWriter();
			template.process(this.map, writer);		// Merge the data-model and the template
		} catch (Exception e) {
			throw new ActionException(String.format("模板渲染错误:%s", e.getMessage()), e);
		}
		finally {
			if (writer != null)
				writer.close();
		}
	}
}

