/**
 * ParamWrapper.java
 * DAO参数解析创建
 * 
 * @author		zhoubing
 * @date   		Jul 16, 2011
 * @revision	v1.0
 */
package org.jftone.jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jftone.dao.DataSort;
import org.jftone.exception.CommonException;
import org.jftone.exception.DaoException;
import org.jftone.model.Model;
import org.jftone.util.IData;
import org.jftone.util.ObjectUtil;

public class TableStructureWrapper {
	
	private List<String> parseFields;				//库表中的参数字段
	private List<Object> parseValues;				//SQL参数字段的对应值
	private List<JdbcType> parseTypes;		//SQL参数字段的对应值的数据类型
	
	
	private TableStructure tableStructure;	//存放某个表结构的所有字段对象
	private Map<String, FieldStructure> fieldMap = new HashMap<String, FieldStructure>();	//存放某个表结构的所有字段对象
	
	/**
	 * 创建ParamWrapper对象
	 * 限制实例化
	 * @param modelName
	 */
	private TableStructureWrapper(TableStructure tableStructure){
		this.tableStructure = tableStructure;
		fieldMap = tableStructure.getFieldData();
	}
	
	
	/**
	 * 获取ParamWrapper方法一
	 * @param model	实体对象
	 * @return
	 */
	public static TableStructureWrapper getWrapper(TableStructure tableStructure){
		return new TableStructureWrapper(tableStructure);
	}

	/**
	 * 返回表的所有字段
	 * @return
	 */
	public Map<String, FieldStructure> getFieldMap() {
		return fieldMap;
	}

	public List<String> getParseFields() {
		return parseFields;
	}
	public void setParseFields(List<String> parseFields) {
		this.parseFields = parseFields;
	}
	public List<Object> getParseValues() {
		return parseValues;
	}
	public void setParseValues(List<Object> parseValues) {
		this.parseValues = parseValues;
	}
	public List<JdbcType> getParseTypes() {
		return parseTypes;
	}
	public void setParseTypes(List<JdbcType> parseTypes) {
		this.parseTypes = parseTypes;
	}


	/**
	 * 解析SQL新增的参数字段
	 * @param model
	 * @return
	 * @throws DaoException
	 * @throws CommonException 
	 */
	public <T extends Model> void parseInsertModel(T model) throws DaoException, CommonException{
		String propertyName = null;
		FieldStructure field = null;
		String fieldName = null;
		boolean flag = tableStructure.getGenerateType() == TableStructure.PK_NATIVE;
		List<String> sqlFields = new ArrayList<String>();
		List<Object> paramOjects = new ArrayList<Object>();
		List<JdbcType> paramTypes = new ArrayList<JdbcType>();
		for(Entry<String, FieldStructure> entry : fieldMap.entrySet()){
			propertyName = entry.getKey();
			field = entry.getValue();
			fieldName = field.getName();
			if(fieldName.equals(tableStructure.getPrimaryKey()) 
					&& flag){
				continue;
			}
			sqlFields.add(fieldName);
			//获取参数字段的值存入对象中
			paramTypes.add(field.getType());
			paramOjects.add(ObjectUtil.getProperty(model, propertyName));
		}
		setParseFields(sqlFields);
		setParseValues(paramOjects);
		setParseTypes(paramTypes);
	}
	
	/**
	 * 解析SQL修改的参数字段,过滤为空的参数
	 * @param model
	 * @return
	 * @throws DaoException
	 * @throws CommonException 
	 */
	public <T extends Model> void parseNotNullModel(T model) throws DaoException, CommonException{
		String propertyName = null;
		List<String> editFields = new ArrayList<>();
		List<Object> paramOjects = new ArrayList<>();
		List<JdbcType> paramTypes = new ArrayList<>();
		FieldStructure field = null;
		Object fieldValue = null;
		String fieldName = null;
		for(Entry<String, FieldStructure> entry : fieldMap.entrySet()){
			propertyName = entry.getKey();
			field = entry.getValue();
			fieldName = field.getName();
			//过滤，不更新主键字段
			if(fieldName.equals(tableStructure.getPrimaryKey())){
				continue;
			}
			fieldValue = ObjectUtil.getProperty(model, propertyName);
			//如果当前字段值为空，则跳过不处理
			if(null == fieldValue){
				continue;
			}
			editFields.add(fieldName);
			paramOjects.add(fieldValue);
			paramTypes.add(field.getType());
		}
		setParseFields(editFields);
		setParseValues(paramOjects);
		setParseTypes(paramTypes);
	}
	
	public <T extends Model> void parseDataMap(IData<String, Object> data) throws DaoException, CommonException{
		String propertyName = null;
		List<String> sqlFields = new ArrayList<>();
		List<Object> paramOjects = new ArrayList<>();
		List<JdbcType> paramTypes = new ArrayList<>();
		FieldStructure field = null;
		Object fieldValue = null;
		for(Entry<String, Object> entry : data.entrySet()){
			propertyName = entry.getKey();
			fieldValue = entry.getValue();
			field = fieldMap.get(propertyName);
			
			sqlFields.add(field.getName());
			paramOjects.add(fieldValue);
			paramTypes.add(field.getType());
		}
		setParseFields(sqlFields);
		setParseValues(paramOjects);
		setParseTypes(paramTypes);
	}
	
	public List<SqlSort> parseSortList(List<DataSort> dss) throws DaoException, CommonException{
		List<SqlSort> sqlSortList = new ArrayList<>();
		for(DataSort ds : dss){
			SqlSort ss = new SqlSort();
			if(!fieldMap.containsKey(ds.getProperty())) {
				continue;
			}
			ss.setFieldName(fieldMap.get(ds.getProperty()).getName());
			ss.setSort(ds.getSort());
			
			sqlSortList.add(ss);
		}
		return sqlSortList;
	}
	
	/**
	 * 解析SQL WHERE条件的参数字段
	 * 只获取关键字字段
	 * @return
	 * @throws DaoException
	 */
	public List<String> getPkField() throws DaoException{
		List<String> pkFields = new ArrayList<>();
		pkFields.add(tableStructure.getPrimaryKey());
		return pkFields;
	}
	
	/**
	 * 获取所有查询字段
	 * @return
	 * @throws DaoException
	 */
	public List<String> getAllField() throws DaoException{
		List<String> sqlFields = new ArrayList<>();
		for(Entry<String, FieldStructure> entry : fieldMap.entrySet()){
			sqlFields.add(entry.getValue().getName());
		}
		return sqlFields;
	}

}
