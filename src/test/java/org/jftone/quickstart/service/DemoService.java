package org.jftone.quickstart.service;

import org.jftone.annotation.Autowired;
import org.jftone.annotation.Service;
import org.jftone.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DemoService {
	private Logger logger = LoggerFactory.getLogger(DemoService.class);
	
	@Autowired
	private Dao dao;
		
	@Autowired
	private UserService userService;
}
