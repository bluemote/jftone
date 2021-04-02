package org.jftone.datasource;

import org.jftone.config.Const;
import org.jftone.jdbc.SqlWrapper;
import org.jftone.jdbc.db2.DB2Wrapper;
import org.jftone.jdbc.mysql.MySQLWrapper;
import org.jftone.jdbc.oracle.OracleWrapper;
import org.jftone.jdbc.postgresql.PostgreSQLWrapper;
import org.jftone.jdbc.sqlite.SQLiteWrapper;
import org.jftone.jdbc.sqlserver.SQLServerWrapper;

public enum DBType {
	MYSQL("mysql"){
		@Override
		SqlWrapper getSqlWrapper() {
			return new MySQLWrapper();
		}

		@Override
		String getDriverName() {
			return Const.JDBC_DRIVER_MYSQL;
		}
	},
	SQLSERVER("sqlserver"){
		@Override
		SqlWrapper getSqlWrapper() {
			return new SQLServerWrapper();
		}

		@Override
		String getDriverName() {
			return Const.JDBC_DRIVER_SQLSERVER;
		}
	},
	ORACLE("oracle"){
		@Override
		SqlWrapper getSqlWrapper() {
			return new OracleWrapper();
		}

		@Override
		String getDriverName() {
			return Const.JDBC_DRIVER_ORACLE;
		}
	},
	DB2("db2"){
		@Override
		SqlWrapper getSqlWrapper() {
			return new DB2Wrapper();
		}

		@Override
		String getDriverName() {
			return Const.JDBC_DRIVER_DB2;
		}
	},
	SQLITE("sqlite"){
		@Override
		SqlWrapper getSqlWrapper() {
			return new SQLiteWrapper();
		}

		@Override
		String getDriverName() {
			return Const.JDBC_DRIVER_SQLITE;
		}
	},
	POSTGRESQL("postgresql"){
		@Override
		SqlWrapper getSqlWrapper() {
			return new PostgreSQLWrapper();
		}

		@Override
		String getDriverName() {
			return Const.JDBC_DRIVER_SQLITE;
		}
	};
	private String code;
	DBType(String code){
		this.code = code;
	}
	
	public String code() {
		return this.code;
	}
	
	/**
	 * 根据数据库code返回对应的数据库枚举类型
	 * @param code
	 * @return
	 */
	public static DBType getDBType(String code){
		DBType ret = null;
		DBType[] dts = DBType.values();
		for(DBType dbtype : dts){
			if(code.equals(dbtype.code)){
				ret = dbtype;
				break;
			}
		}
		return ret;
	}
	
	/**
	 * 返回对应数据库的sql封装对象
	 * @return SqlWrapper
	 */
	abstract SqlWrapper getSqlWrapper();
	
	/**
	 * 获取驱动包路径全名
	 * @return
	 */
	abstract String getDriverName();
}
