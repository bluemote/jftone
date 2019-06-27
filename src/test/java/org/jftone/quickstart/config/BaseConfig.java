package org.jftone.quickstart.config;

import java.util.ArrayList;
import java.util.List;

import org.jftone.config.ActionInterceptor;
import org.jftone.config.AppConfig;
import org.jftone.config.Route;

public class BaseConfig implements AppConfig {
	public void loadRoute(Route route) {
		/**等同于在Action上增加@Controller(mapping="/admin/userAction"), 二者取其一
		route.add("/userAction", UserAction.class);
		*/
		
	}

	@Override
	public List<ActionInterceptor> loadInterceptor() {
		List<ActionInterceptor> list = new ArrayList<ActionInterceptor>();
		list.add(new BaseInterceptor());
		return list;
	}
}
