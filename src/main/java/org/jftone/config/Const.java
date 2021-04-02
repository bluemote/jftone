package org.jftone.config;

import org.jftone.action.render.ViewType;

public class Const {
	
	public static final String BASE_PACKAGE = "org.jftone.data";
	
	public static final String MYSQL = "mysql";
	public static final String SQLSERVER = "sqlserver";
	public static final String ORACLE = "oracle";
	public static final String DB2 = "db2";
	
	public static final String CHARSET_UTF8 = "UTF-8";	
	
	public static final ViewType DEFAULT_VIEW_TYPE = ViewType.FREE_MARKER;
	
	public static final String DEFAULT_UPLOAD_PATH = "upload";
	
	public static final String DEFAULT_DOWNLOAD_PATH = "download";
	
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	public static final String DEFAULT_JSP_EXT = ".jsp";
	
	public static final String DEFAULT_FREE_MARKER_EXT = ".html";			// The original is ".ftl", Recommend ".html"
	
	public static final int DEFAULT_MAX_POST_SIZE = 1024 * 1024 * 5;  			// Default max post size of multipart request: 10 Meg
	
	public static final int DEFAULT_FREEMARKER_TEMPLATE_UPDATE_DELAY = 5;	// For not devMode only
	
	public static final String DAO_METHOE = "setDao";			//Component注入默认Dao方法名
	
	public static final String CONTENT_TEXT = "text/plain";
	public static final String CONTENT_JSON = "application/json";
	public static final String CONTENT_HTML = "text/html";
	public static final String CONTENT_JAVASCRIPT = "application/javascript";
	
	public static final String URL_PARTERN_STATIC = "static";

	public static final String SPLIT_COMMA = ",";
	public static final String SYMBOL_POINT = ".";
	
	public static final String CONFIG_XML = "xml";
	
	public static final String SCOPE_SINGLETON = "singleton";
	public static final String SCOPE_PROTOTYPE = "prototype";
	
	public static final String JDBC_DRIVER_MYSQL = "com.mysql.jdbc.Driver";
	public static final String JDBC_DRIVER_SQLSERVER = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
	public static final String JDBC_DRIVER_ORACLE = "oracle.jdbc.driver.OracleDriver";
	public static final String JDBC_DRIVER_DB2 = "com.ibm.db2.jcc.DB2Driver"; // com.ibm.db2.jdbc.app.DB2Driver
	public static final String JDBC_DRIVER_SQLITE = "org.sqlite.JDBC";
	public static final String JDBC_DRIVER_POSTGRESQL = "org.postgresql.Driver";

}

