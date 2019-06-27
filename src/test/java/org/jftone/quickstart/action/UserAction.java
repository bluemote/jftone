package org.jftone.quickstart.action;

import javax.servlet.http.HttpServletRequest;

import org.jftone.action.ActionSupport;
import org.jftone.annotation.Autowired;
import org.jftone.annotation.Controller;
import org.jftone.exception.ActionException;
import org.jftone.quickstart.model.User;
import org.jftone.quickstart.service.UserService;
import org.jftone.service.BaseService;
import org.jftone.util.IData;
import org.jftone.util.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller(mapping="/admin/userAction")
public class UserAction extends ActionSupport {

	private Logger logger = LoggerFactory.getLogger(UserAction.class);
	
	
	@Autowired
	private BaseService service;		//封装了其他基本操作，后期版本考虑移除
	
	@Autowired
	private UserService userService;
	
	/**
	 * 下面可以理解为从页面提交过来的name值  ， 也可以从 getData获取，支持基本数据类型值
	 */
	private String name;
	public void setName(String name) {
		this.name = name;
	}
	/**
	 *  默认访问方法  http://localhost/admin/userAction
	 * 如果配置为 do结束 则为  http://localhost/admin/userAction.do
	 * 
	 * @throws ActionException
	 */
	public void execute() throws ActionException{
		
	}
	/**
	 * 默认访问访问路径  http://localhost/admin/userAction/query
	 * 如果配置为 do结束 则为  http://localhost/admin/userAction.do?method=query
	 * @throws ActionException
	 */
	public void query() throws ActionException{
		IData<String, Object> data = getData();		//封装有从页面form表单提交，或者get请求参数
		
		//name值也可以在 UserAction 中声明  private String name;  再增加setName
		String name =  data.getString("name", "defaultValue");	
		//上面等同于
		HttpServletRequest request = getRequest();		//getRequest()  getResponse() getData 都是从ActionSupport获取
		name = request.getParameter("name");
		try {
			User user = new User();
			user.setUserName(name);
			
			Page page = this.getPage();	//可以继续设置分页参数
			
			userService.query(user, page);
			
			//从WEB-INF下面的配置的目录 html/admin目录下查询文件，文件是采用freemarker模板
			this.render("admin/user.html");
			
		} catch (Exception e) {
			logger.error("查询角色根节点数据错误", e);
			throw new ActionException("查询角色根节点数据错误", e);
		}
		
	}
	
}
