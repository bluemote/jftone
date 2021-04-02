/**
 * SqlConfigLoad.java
 * 扫描SQL配置文件
 * 
 * @author		zhoubing
 * @date   		May 1, 2012
 * @revision	v1.0
 */
package org.jftone.code;

import java.io.File;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jftone.config.Const;
import org.jftone.exception.ActionException;
import org.jftone.jdbc.DBRepository;
import org.jftone.jdbc.SqlParser;
import org.jftone.jdbc.SqlStructure;
import org.jftone.util.FileUtil;
import org.jftone.util.StringUtil;

/**
 * @author zhoubing
 * 
 */
public class SqlConfigLoad {
	private Logger log = LoggerFactory.getLogger(SqlConfigLoad.class);
	public static final String SQL_CONFIG_FILE = "sql-config.xml";

	/**
	 * 加载特定配置文件，文件以当前类上下文为基准路径
	 * 
	 * @param sqlConfigFile
	 *            ${classpath}/path/file
	 * @throws Exception 
	 */
	public void loadSQL(String sqlConfigFile) throws Exception {
		String[] resources = null;
		if(null == sqlConfigFile || "".equals(sqlConfigFile)){
			return;
		}
		if(sqlConfigFile.indexOf(Const.SPLIT_COMMA)>-1){
			resources = sqlConfigFile.split(Const.SPLIT_COMMA);
		}else{
			resources = new String[]{sqlConfigFile};
		}
		File file = null;
		for(String sqlFile : resources){
			file = FileUtil.loadClasspathFile(sqlFile);
			if(file == null || !file.exists()){
				throw new ActionException("没有找到资源文件："+file.getCanonicalPath());
			}
			parseSQL(file);
		}
	}

	/**
	 * 解析配置文件
	 * @param sqlfile
	 * @throws DocumentException 
	 * @throws ActionException
	 */
	@SuppressWarnings("unchecked")
	private void parseSQL(File sqlfile) throws ActionException, DocumentException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(sqlfile);
		List<Element> nodes = doc.selectNodes("sqlMap/statement");
		if (null == nodes || nodes.size() == 0) {
			return;
		}
		String sqlKey;
		String sqlValue;
		boolean parse;
		for (Element el : nodes) {
			sqlKey = el.attributeValue("id");
			if(DBRepository.existSQLStatement(sqlKey)){
				log.error("已经存在["+sqlKey+"]关键字的SQL配置");
				throw new ActionException("已经存在["+sqlKey+"]关键字的SQL配置");
			}
			sqlValue = el.getTextTrim();
			SqlStructure sqlObj = new SqlStructure(sqlKey);
			sqlObj.setSql(sqlValue);
			String tmpParse = el.attributeValue("parse");
			if(null != tmpParse && tmpParse.equals("true")){
				parse = true;
			}else{
				parse = StringUtil.find(sqlValue, SqlParser.COND_PREFIX
						+SqlParser.PARAM_PATTERN+SqlParser.COND_SUFFIX);
			}
			sqlObj.setParse(parse);
			SqlParser.assembleSql(sqlObj);
			DBRepository.putStatement(sqlKey, sqlObj);
		}	
	}
}
