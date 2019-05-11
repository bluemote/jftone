package org.jftone.jdbc.handler;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jftone.dao.DaoUtil;
import org.jftone.exception.DbException;
import org.jftone.jdbc.FieldStructure;
import org.jftone.model.Model;
import org.jftone.model.ModelRepository;
import org.jftone.util.ObjectUtil;

import com.esotericsoftware.reflectasm.MethodAccess;

public class BeanListHandler<T extends Model> implements ResultSetHandler<List<T>> {
	
	private Class<T> modelClazz;
	
	private Map<String, FieldStructure> fieldMap;
	
	public BeanListHandler(Class<T> modelClazz){
		this.modelClazz = modelClazz;
	}
	
	public BeanListHandler(Class<T> modelClazz, Map<String, FieldStructure> fieldMap){
		this.modelClazz = modelClazz;
		this.fieldMap = fieldMap;
	}
	
	@Override
	public List<T> handle(ResultSet rs) throws DbException {
		List<T> list = new ArrayList<T>();
		try {
			Field[] fields = null;
			String[] props = null;
			MethodAccess access = ModelRepository.get(modelClazz);
			while(rs.next()){
				T model = modelClazz.newInstance();
				ResultSetMetaData rsMeta = rs.getMetaData();
				int column = rsMeta.getColumnCount();
				if(fieldMap == null){
					if(fields == null){
						String propertyName = null;
						fields = new Field[column];
						for(int i=1; i<= column; i++){
							propertyName = ObjectUtil.getPropertyName(rsMeta.getColumnName(i));
							fields[i-1] = model.getClass().getDeclaredField(propertyName);	//获取属性对象，取得其数据类型
						}
					}
					Field field = null;
					for(int i=1; i<= column; i++){
						field = fields[i-1];
						access.invoke(model, ObjectUtil.getSetter(field.getName()), DaoUtil.getJdbcValue(rs, field.getType(), i));
					}
				}else{
					if(props == null){
						props = new String[column];
						for(int i=1; i<= column; i++){
							props[i-1] = ObjectUtil.getPropertyName(rsMeta.getColumnName(i));
						}
					}
					for(int i=1; i<= column; i++){
						access.invoke(model, ObjectUtil.getSetter(props[i-1]), DaoUtil.getJdbcValue(rs, fieldMap.get(props[i-1]).getType(), i));
					}
				}
				list.add(model);
			}
			fields = null;
			props = null;
		} catch (Exception e) {
			throw new DbException(e);
		}
		return list;
	}

}
