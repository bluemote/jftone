/**
 * CodeGenerate.java
 * java代码生成
 * 
 * @author		zhoubing
 * @date   		May 22, 2012
 * @revision	v1.0
 */
package org.jftone.code;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jftone.config.Const;
import org.jftone.config.PropertyConfigurer;
import org.jftone.exception.ActionException;
import org.jftone.jdbc.DBRepository;
import org.jftone.jdbc.FieldStructure;
import org.jftone.jdbc.JdbcType;
import org.jftone.jdbc.TableStructure;
import org.jftone.util.DateUtil;

import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class CodeGenerate {
	private Log log = LogFactory.getLog(CodeGenerate.class);
	
	private String entityVM = "Model.tpl";
	
	private String entityPackage = "org.jftone.model";

	/**
	 * 产生代码
	 * 
	 * @param projectDir
	 *            java文件生成的根目录
	 * @throws ActionException
	 * @throws IOException 
	 */
	public void generateCode(String projectDir, boolean recover) throws ActionException, IOException {
		Template template = getTemplate(entityVM);
		if (null == template) {
			throw new ActionException("模板文件加载错误，请确认模板文件:" + entityVM);
		}
		Map<String, TableStructure> tblData = DBRepository.getTableMart();
		String generateFile = "";
		for(Entry<String, TableStructure> entry : tblData.entrySet()){
			String entityName = entry.getKey();
			TableStructure table = entry.getValue();
			Map<String, Object> map = parseEntityStructure(entityName, table);
			generateFile = this.getFile(projectDir, PropertyConfigurer.get(PropertyConfigurer.MODEL_PACKAGE),
					entityName);
			File file = new File(generateFile);
			if(file.exists()){
				if(!recover) continue;		//如果存在则取消覆盖
			}
			log.debug("开始生成model对象："+generateFile);
			this.makeFile(template, generateFile, map);
		}
	}

	private String getFile(String projectDir, String packageName,
			String entityName) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(projectDir);
		if (!projectDir.endsWith(File.separator)) {
			sb.append(File.separator);
		}
		// 将包路径替换为文件分隔符
		sb.append(packageName.replace(".", File.separator));
		sb.append(File.separatorChar);
		File file = new File(sb.toString());
		if(!file.exists()){
			file.mkdirs();
		}
		sb.append(entityName + ".java");
		return sb.toString();
	}

	/**
	 * 解析表结构数据
	 * 
	 * @param entityName
	 * @param table
	 * @param template
	 * @throws ActionException
	 */
	private Map<String, Object> parseEntityStructure(String entityName, TableStructure table)
			throws ActionException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		String packagePath = PropertyConfigurer.get(PropertyConfigurer.MODEL_PACKAGE);
		List<Map<String, String>> proList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> methodList = new ArrayList<Map<String, String>>();
		Map<String, String> packageMap = new HashMap<String, String>();
		Map<String, FieldStructure> fieldData = table.getFieldData();
		for(Entry<String, FieldStructure> entry : fieldData.entrySet()){
			String propertyName = entry.getKey();
			FieldStructure field = entry.getValue();
			parseProperty(proList, propertyName, field, table);
			parseMethod(methodList, propertyName, field);
		}
		//判断是否需要导入特殊属性类型对象包，比如时间，大精度对象等
		importPropertyPackage(proList, packageMap);
		//判断实体子类是否和实体类在同一个包下面
		if(!packagePath.equals(entityPackage)){
			packageMap.put(entityPackage + ".Model", entityPackage + ".Model");
		}
		//加入注解包
		packageMap.put("javax.persistence.Column", "javax.persistence.Column");
		packageMap.put("javax.persistence.Entity", "javax.persistence.Entity");
		packageMap.put("javax.persistence.GeneratedValue", "javax.persistence.GeneratedValue");
		packageMap.put("javax.persistence.GenerationType", "javax.persistence.GenerationType");
		packageMap.put("javax.persistence.Id", "javax.persistence.Id");
		packageMap.put("javax.persistence.Table", "javax.persistence.Table");

		map.put("entity", entityName); // 实体名字
		map.put("tableName", table.getName()); // 实体名字
		map.put("date", DateUtil.getNowStr()); // 时间
		map.put("package", packagePath); // 类包
		map.put("propertys", proList); // 属性
		map.put("methods", methodList); // 方法
		if (packageMap.size() > 0) {
			map.put("imports", packageMap); // 导入包
		}
		return map;
	}

	/**
	 * 导入普通属性对象
	 * 
	 * @param proList
	 * @param packageMap
	 */
	private void importPropertyPackage(List<Map<String, String>> proList, Map<String, String> packageMap) {
		if (null == proList || proList.size() == 0) {
			return;
		}
		String dataType = "";
		for (Map<String, String> map : proList) {
			dataType = map.get("type");
			if (dataType.equals("Date")) {
				packageMap.put("java.util.Date", "java.util.Date");
			} else if (dataType.equals("BigDecimal")) {
				packageMap.put("java.math.BigDecimal", "java.math.BigDecimal");
			}
		}
	}

	/**
	 * 组装属性数据
	 * 
	 * @param proList
	 * @param propertyName
	 * @param field
	 */
	private void parseProperty(List<Map<String, String>> proList, String propertyName,
			FieldStructure field, TableStructure table) {
		Map<String, String> proMap = new HashMap<String, String>();
		proMap.put("name", propertyName);
		proMap.put("fieldName", field.getName());
		if(field.getType() == JdbcType.DATE){
			proMap.put("columnDefinition", "date");
		}else if(field.getType() == JdbcType.TIME){
			proMap.put("columnDefinition", "time");
		}else if(field.getType() == JdbcType.DATETIME){
			proMap.put("columnDefinition", "datetime");
		}
		proMap.put("type", field.getType().toJavaType());
		if(field.getName().equals(table.getPrimaryKey())){
			int type = table.getGenerateType();
			proMap.put("id", "true");
			if(type == TableStructure.PK_NATIVE){
				proMap.put("strategy", "GenerationType.IDENTITY");
			}else if(type == TableStructure.PK_SEQUENCE){
				proMap.put("strategy", "GenerationType.SEQUENCE");
			}else if(type == TableStructure.PK_CUSTOMIZE){
				proMap.put("strategy", "GenerationType.AUTO");
			}
		}
		proList.add(proMap);
	}

	/**
	 * 组装方法属性数据
	 * 
	 * @param methodList
	 * @param propertyName
	 * @param field
	 */
	private void parseMethod(List<Map<String, String>> methodList, String propertyName,
			FieldStructure field) {
		Map<String, String> methodMap = new HashMap<String, String>();
		methodMap.put("field", propertyName);
		methodMap.put("method", propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1));
		methodMap.put("type", field.getType().toJavaType());
		methodList.add(methodMap);
	}


	/**
	 * 获取模板
	 * 
	 * @param templateFile
	 * @return
	 */
	private Template getTemplate(String templateFile) {
		Template template = null;
		Configuration config = new Configuration(Configuration.VERSION_2_3_23);
		try {
			
			// 获取当前类路径
			String path = this.getClass().getResource("").getPath();
			config.setDirectoryForTemplateLoading(new File(path));
			config.setObjectWrapper(new BeansWrapperBuilder(Configuration.VERSION_2_3_23).build());
	        config.setDefaultEncoding(Const.CHARSET_UTF8);
	        config.setOutputEncoding(Const.CHARSET_UTF8);
	        config.setLocale(Locale.CHINESE);
	        config.setLocalizedLookup(false);
			template = config.getTemplate(templateFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return template;
	}

	/**
	 * 生成文件
	 * 
	 * @param template
	 * @param generateFile
	 * @param map
	 */
	private void makeFile(Template template, String generateFile,
			Map<String, Object> map) {
		if (null == map || map.size() < 1) {
			return;
		}
		try {
			File tmpfile = new File(generateFile);
            Writer out = new OutputStreamWriter(new FileOutputStream(tmpfile));
            template.process(map, out);
            out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
