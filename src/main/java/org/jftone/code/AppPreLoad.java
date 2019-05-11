package org.jftone.code;

import org.jftone.datasource.DataSourceContext;
import org.jftone.jdbc.DBScan;
import org.jftone.jdbc.DBScanMySql;
import org.jftone.listener.JFToneApp;

public class AppPreLoad {
	
	public static void main(String[] arg) throws Exception{
		try{
			//加载配置及数据源
			JFToneApp.initialized();
			
			//扫描数据库表
			DBScan scan = new DBScanMySql();
			scan.setConn(DataSourceContext.getDataSource().getConnection());
			scan.scanDBTable();
			
			//生成模型对象文件
			CodeGenerate codeGenerate = new CodeGenerate();
			//codeGenerate.generateCode("D:\\Project\\Lamapai\\", true);
			codeGenerate.generateCode("D:\\Project\\Carbid\\java", true);
			//codeGenerate.generateCode("E:\\Project\\HbyWeb\\", true);
		}finally{
			JFToneApp.destroyed();
		}
		
	}
}
