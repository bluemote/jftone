package org.jftone.datasource;

import java.sql.Connection;

public final class ConnectionHolder {
	
	private Connection connection;		//数据源连接对象
	
	private int referenceCount = 0;		//数据源连接引用次数

	
	public ConnectionHolder(Connection connection){
		this.connection = connection;
	}
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	public Connection getConnection() { 
        return this.connection;
    }
	
	public void requested(){
		this.referenceCount++;
	}
	
	public void released() {
        this.referenceCount--;
	}
	
	public int getReferenceCount() {
		return this.referenceCount;
	}
}
