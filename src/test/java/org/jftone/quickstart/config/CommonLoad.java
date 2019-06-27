package org.jftone.quickstart.config;

import org.jftone.config.AppListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonLoad implements AppListener {
	private Logger logger = LoggerFactory.getLogger(CommonLoad.class);
	
	@Override
	public void load() {
		try {
			//业务启动执行作业
			
			
		} catch (Exception e) {
			logger.error("启动业务加载错误",  e);
		}
	}

	public void destroy() {
		try {
			
		} catch (Exception e) {
			logger.error("注销业务数据错误", e);
		}
	}
}
