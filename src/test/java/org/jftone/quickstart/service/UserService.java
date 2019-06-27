package org.jftone.quickstart.service;

import java.util.List;

import org.jftone.annotation.Autowired;
import org.jftone.annotation.Service;
import org.jftone.annotation.Transactional;
import org.jftone.dao.Dao;
import org.jftone.exception.DaoException;
import org.jftone.exception.ServiceException;
import org.jftone.quickstart.model.User;
import org.jftone.util.DataMap;
import org.jftone.util.IData;
import org.jftone.util.Page;
import org.jftone.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService{
	
	private Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private Dao dao;
	
	@Autowired
	private DemoService demoService;		//DemoService 和  UserService可以相互引用
	
	/**
	 * Transactional 表示启用事务，如果在jftone配置中指定，也不需要配置，如果配置中没有，那么以注解为准
	 * @param user
	 * @throws ServiceException
	 */
	@Transactional
	public List<IData<String, Object>> query(User user, Page page) throws ServiceException{
		List<IData<String, Object>> retList = null;
		try {
			IData<String, Object> inData = new DataMap<String, Object>();
			if(!StringUtil.isBlank(user.getUserNo())) {
				inData.put("USER_NO", user.getUserNo());
			}
			if(!StringUtil.isBlank(user.getUserName())) {
				inData.put("USER_NAME", user.getUserName());
			}
			//queryUser表示从jftone配置文件中KEY：sqlConfig 指定的 sql配置文件中配置项id为queryUser的语句
			retList = dao.query("queryUser", inData, page);
			
		} catch (Exception e) {
			logger.error("查询数据信息错误", e);
			throw new ServiceException("查询数据信息错误", e);
		}
		return retList;
	}
	
}
