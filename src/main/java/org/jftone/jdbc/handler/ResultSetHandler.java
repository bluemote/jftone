/**
 * DBUtil.java
 * 
 * @author		zhoubing
 * @date   		Jul 5, 2011
 * @revision	v1.0
 */
package org.jftone.jdbc.handler;

import java.sql.ResultSet;

import org.jftone.exception.DbException;


public interface ResultSetHandler<T> {
	

	public T handle(ResultSet rs) throws DbException;
	 
}
