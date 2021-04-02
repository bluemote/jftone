package org.jftone.jdbc.handler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.jftone.exception.DbException;
import org.jftone.util.DataMap;
import org.jftone.util.IData;

public class MapHandler implements ResultSetHandler<IData<String, Object>> {
	
	@Override
	public IData<String, Object> handle(ResultSet rs) throws DbException {
		IData<String, Object> data = new DataMap<String, Object>();
		try {
			if(rs.next()){
				if(rs.getFetchSize()>1) {
					throw new DbException("查询结果返回多行记录数据");
				}
				ResultSetMetaData rsMeta = rs.getMetaData();
				int column = rsMeta.getColumnCount();
				for(int i=1; i<= column; i++){
					String fieldName = rsMeta.getColumnName(i).toUpperCase();
					data.put(fieldName, rs.getObject(i));
				}
			}
		} catch (Exception e) {
			throw new DbException("ResultSet映射IData错误", e);
		}
		return data;
	}

}
