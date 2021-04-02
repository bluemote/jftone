package org.jftone.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class JFToneListener implements ServletContextListener {
	
	public void contextInitialized(ServletContextEvent sce) {
		JFToneLauncher.start(sce.getServletContext()
				.getInitParameter("appConfig"));
	}

	public void contextDestroyed(ServletContextEvent sce) {
		JFToneLauncher.stop();
	}
}
