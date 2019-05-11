package org.jftone.jdbc.handler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import org.jftone.exception.DbException;
import org.jftone.util.DataMap;
import org.jftone.util.IData;

public class MapListHandler implements ResultSetHandler<List<IData<String, Object>>> {
	
	@Override
	public List<IData<String, Object>> handle(ResultSet rs) throws DbException {
		List<IData<String, Object>> list = new ArrayList<IData<String, Object>>();
		try {
			String[] props = null;
			while(rs.next()){
				IData<String, Object> data = new DataMap<String, Object>();
				ResultSetMetaData rsMeta = rs.getMetaData();
				int column = rsMeta.getColumnCount();
				if(props == null){
					props = new String[column];
					for(int i=1; i<= column; i++){
						props[i-1] = rsMeta.getColumnName(i).toUpperCase();
					}
				}
				for(int i=1; i<= column; i++){
					data.put(props[i-1], rs.getObject(i));
				}
				list.add(data);
			}
			props = null;
		} catch (Exception e) {
			throw new DbException(e);
		}
		return list;
	}

}
