package org.jftone.jdbc.handler;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;

import org.jftone.dao.DaoUtil;
import org.jftone.exception.DbException;
import org.jftone.jdbc.FieldStructure;
import org.jftone.model.Model;
import org.jftone.model.ModelRepository;
import org.jftone.util.ObjectUtil;

import com.esotericsoftware.reflectasm.MethodAccess;

public class BeanHandler<T extends Model> implements ResultSetHandler<T> {
	
	private Class<T> modelClazz;
	
	private Map<String, FieldStructure> fieldMap;
	
	public BeanHandler(Class<T> modelClazz){
		this.modelClazz = modelClazz;
	}
	
	public BeanHandler(Class<T> modelClazz, Map<String, FieldStructure> fieldMap){
		this.modelClazz = modelClazz;
		this.fieldMap = fieldMap;
	}
	
	@Override
	public T handle(ResultSet rs) throws DbException {
		T model = null;
		try {
			if(rs.next()){
				if(rs.getFetchSize()>1) {
					throw new DbException("查询结果返回多行记录数据");
				}
				model = modelClazz.newInstance();
				String propertyName = null;
				Field field = null;
				ResultSetMetaData rsMeta = rs.getMetaData();
				int column = rsMeta.getColumnCount();
				MethodAccess access = ModelRepository.get(modelClazz);
				Object value = null;
				for(int i=1; i<= column; i++){
					propertyName = ObjectUtil.getPropertyName(rsMeta.getColumnName(i));
					if(null == fieldMap){
						field = model.getClass().getDeclaredField(propertyName);	//获取属性对象，取得其数据类型
						value = DaoUtil.getJdbcValue(rs, field.getType(), i);
					}else{
						value = (fieldMap.get(propertyName).getType()).getJdbcValue(rs, i);
					}
					access.invoke(model, ObjectUtil.getSetter(propertyName), value);
				}
			}
		} catch (Exception e) {
			throw new DbException("ResultSet映射Model错误", e);
		}
		return model;
	}

}
