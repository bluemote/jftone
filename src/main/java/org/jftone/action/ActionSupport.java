/**
 * ActionSupport.java
 * 所有ActionSupport实现接口
 * 
 * @author		zhoubing
 * @date   		Apr 14, 2012
 * @revision	v1.0
 */
package org.jftone.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jftone.action.render.Render;
import org.jftone.action.render.RenderFactory;
import org.jftone.config.Const;
import org.jftone.exception.ActionException;
import org.jftone.util.DataMap;
import org.jftone.util.IData;
import org.jftone.util.ObjectUtil;
import org.jftone.util.Page;

import com.esotericsoftware.reflectasm.MethodAccess;

/**
 * @author zhoubing
 *
 */
public class ActionSupport implements Action {
	private Logger log = LoggerFactory.getLogger(ActionSupport.class);
	private MessageResource messageResource;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private MethodAccess methodAccess;
	private String actionMethod;
	private final String CURRENT_PAGE = "curPage";	//请求页面
	private Page page = new Page();							//分页对象，默认每页显示20，当前为1页
	private IData<String, Object> data = new DataMap<String, Object>();			//存放请求的request参数
	private Map<String, Object> map = new HashMap<String, Object>();
	
	private static final String POST_METHOD = "POST";
	private static final String MULTIPART = "multipart/";

	/**
	 * 返回Request对象
	 * @return
	 */
	public HttpServletRequest getRequest(){
		return request;
	}
	/**
	 * 返回Response对象
	 * @return
	 */
	public HttpServletResponse getResponse(){
		return response;
	}
	/**
	 * 获取当前正在执行的方法名
	 * @return
	 */
	public String getCurActionMethod(){
		return actionMethod;
	}
	/**
	 * 返回页面分页对象
	 * @return
	 */
	public Page getPage(){
		return this.page;
	}
	
	/**
	 * 返回页面请求的的parameter参数值
	 * 已经封装成Data对象
	 * @return
	 */
	public IData<String, Object> getData(){
		return data;
	}
	
	/**
	 * 返回国际化资源信息
	 * @return the messageResource
	 */
	public final MessageResource getMessageResource() {
		return messageResource;
	}
	
	/**
	 * 初始化请求参数
	 */
	@Override
	public final void initReqParameter(ActionProxy proxy) throws ActionException{
		this.request = proxy.getRequest();
		this.response = proxy.getResponse();
		this.methodAccess = proxy.getActionAccess();
		this.actionMethod = proxy.getActionMethod();
		this.messageResource = proxy.getMessageResource();
		
		String contentType = request.getContentType();
		//判断是否有文件上传处理
		if (null == contentType || !request.getMethod().equalsIgnoreCase(POST_METHOD) 
				|| !contentType.toLowerCase().startsWith(MULTIPART)) {
        	//初始化页面parameter数据集
			initData();
			//初始化分页
			initPage();
			//设置页面setter数据
			initSetter();
        }
	}
	/**
	 * 框架执行方法，调用用户自定义Action对象指定方法，不能覆盖
	 * @return
	 * @throws ActionException
	 */
	@Override
	public final void handleRequest() throws ActionException {
		try {
			if(null == actionMethod || actionMethod.trim().length()==0){
				throw new ActionException("不存在方法："+actionMethod);
			}else{
				/**
				 * 调用配置中的指定的对象及方法
				 * 取消this.getClass().getMethod(actionMethod).invoke(this)调用
				 * 改用ReflectASM
				 */
				methodAccess.invoke(this, actionMethod);
			}
		} catch (Exception e) {
			log.debug("执行Action方法错误", e);
			throw new ActionException("执行Action方法错误", e);
		} 
	}
	
	/**
	 * 初始化页面传送过来数据
	 */
	private void initData(){
		Map<String, String[]> paramerterMap = request.getParameterMap();
		if(null == paramerterMap) return;
		String key = null;
		String[] valueObj = null;
		for(Map.Entry<String, String[]> entry : paramerterMap.entrySet()){
			key = entry.getKey();
			valueObj = entry.getValue();
			if(null == valueObj){
				data.put(key, "");
			}else{
				if(valueObj.length<=1){
					data.put(key, valueObj[0]);
				}else{
					data.put(key, valueObj);
				}
			}
		}
	}
	
	/**
	 * 初始化分页
	 */
	private void initPage(){
		page.setCurrentPage(data.getInt(CURRENT_PAGE, 1));
	}
	
	/**
	 * 判断页面
	 * @throws ActionException
	 */
	private void initSetter() throws ActionException {
		int modifier = 0;
		String fieldName = null;
		String fieldType = null;
		try{
			Field[] fieldArray = this.getClass().getDeclaredFields();
			for(Field field : fieldArray){
				modifier = field.getModifiers();
				fieldName = field.getName();
				//过滤final和static修饰的属性赋值
				if(modifier == Modifier.FINAL || modifier == Modifier.STATIC 
						|| modifier == 16 || !data.containsKey(fieldName)){
					continue;
				}
				fieldType = field.getType().getSimpleName().toLowerCase();
				Object value = null;
				if(fieldType.startsWith("int")){
					value = data.getInt(fieldName);
				}else if(fieldType.startsWith("char")){
					value =  data.getChar(fieldName);
				}else if(fieldType.equals("short")){
					value =  data.getShort(fieldName);
				}else if(fieldType.equals("float")){
					value =  data.getFloat(fieldName);
				}else if(fieldType.equals("long")){
					value =  data.getLong(fieldName);
				}else if(fieldType.equals("double")){
					value =  data.getDouble(fieldName);
				}else if(fieldType.equals("boolean")){
					value =  data.getBoolean(fieldName);
				}else if(fieldType.equals("date")){
					value =  data.getDate(fieldName);
				}else if(fieldType.equals("string")){
					value =  data.getString(fieldName);
				}
				if(value != null)
				ObjectUtil.setProperty(this, fieldName, value);		//设置页面对象数据
			}
		}catch(Exception e){
			throw new ActionException("页面参数setter类型转换错误", e);
		}
	}
	
	/**
	 * 加入一组页面渲染对象
	 * @param data
	 * @throws ActionException
	 */
	public final void setRenderData(Map<String, Object> data) {
		map.putAll(data);
	}
	
	/**
	 * 增加单个页面渲染数据
	 * @param key
	 * @param value
	 * @throws ActionException
	 */
	public final void putRenderInfo(String key, Object value) {
		map.put(key, value);
	}
	
	/**
	 * 渲染页面模板 
	 * @param pageFile
	 * @throws ActionException 
	 * @throws ServletException
	 */
	public final void render(String pageFile) throws ActionException {
		map.putAll(this.data);
		Render render = RenderFactory.getInstance().getRender(pageFile, map);
		render.setContext(request, response).render();
	}
	
	/**
	 * 发送字符信息到客户端
	 * @param str
	 * @param contentType
	 * @throws ActionException
	 */
	public final void send(String str, String contentType) throws ActionException{
		response.setCharacterEncoding(Const.CHARSET_UTF8);
		response.setContentType(contentType);
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.write(str);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			throw new ActionException(e);
		}
	}
	
	/**
	 * 页面转发
	 * @param actionUrl
	 * @throws ServletException
	 */
	public final void forward(String actionUrl) throws ActionException {
		if (null == actionUrl) return;
		RequestDispatcher dispatcher = request.getRequestDispatcher(actionUrl);
		try {
			dispatcher.forward(request, response);
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}
	
	/**
	 * 页面重定向
	 * @param actionUrl
	 * @throws ActionException 
	 */
	public final void redirect(String actionUrl) throws ActionException {
		if (null == actionUrl) return;
		try {
			response.sendRedirect(actionUrl);
		} catch (IOException e) {
			throw new ActionException(e);
		}
	}
}
