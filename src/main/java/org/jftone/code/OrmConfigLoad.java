/**
 * OrmConfigLoad.java
 * 扫描model文件加载对象关系映射
 * 
 * @author		zhoubing
 * @date   		May 7, 2012
 * @revision	v1.0
 */
package org.jftone.code;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jftone.config.Const;
import org.jftone.exception.ActionException;
import org.jftone.jdbc.DBRepository;
import org.jftone.jdbc.DataType;
import org.jftone.jdbc.FieldStructure;
import org.jftone.jdbc.JdbcType;
import org.jftone.jdbc.TableStructure;
import org.jftone.util.ClassUtil;

public class OrmConfigLoad {
	private Log log = LogFactory.getLog(OrmConfigLoad.class);
	
	public void scanModel(String packageName) throws Exception{
		String[] pkgNames = null;
		if(packageName.contains(Const.SPLIT_COMMA)){
			pkgNames = packageName.split(Const.SPLIT_COMMA);
		}else{
			pkgNames = new String[]{packageName}; 
		}
		List<String> classes = null;
		for(String pn : pkgNames){
			classes = ClassUtil.getClasses(pn);
			parseModel(classes);
		}
	}

	/**
	 * 根据model注解生成对象关系映射
	 * @throws ActionException
	 */																													
	private void parseModel(List<String> classes) throws ActionException {
		if(null == classes) return;
		try {
			for(String clsName : classes){
				Class<?> entityCls = Class.forName(clsName);
				if(!entityCls.isAnnotationPresent(Table.class)) continue;
				Table tableAnnotation = entityCls.getAnnotation(Table.class);
				TableStructure table = new TableStructure(tableAnnotation.name());
				Field[] fields = entityCls.getDeclaredFields();
				Map<String, FieldStructure> fieldData = new HashMap<String, FieldStructure>();
				for(Field field : fields){
					if(field.isAnnotationPresent(Id.class)){
						parsePrimaryField(field, table);
					}
					if(field.isAnnotationPresent(Column.class)){
						fieldData.put(field.getName(), parseNormalField(field));
					}
				}
				table.setFieldData(fieldData);
				DBRepository.addTable(table);	//装在表数据映射到内存中
			}
		} catch (Exception e) {
			log.error("解析model注解错误", e);
			throw new ActionException(e);
		}
	}

	/**
	 * 解析主键字段
	 * @param field
	 * @param table
	 */
	private void parsePrimaryField(Field field, TableStructure table) {
		Column column = field.getAnnotation(Column.class);
		GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
		table.setPrimaryKey(column.name());
		table.setPrimaryType(DataType.getJdbcType(field.getType()));
		table.setGenerateType(getGenerateType(generatedValue.strategy()));
	}
	/**
	 * 解析普通字段
	 * @param fieldCls
	 * @return
	 */
	private FieldStructure parseNormalField(Field field) {
		Column column = field.getAnnotation(Column.class);
		JdbcType jdbcType = DataType.getJdbcType(field.getType());
		if(jdbcType == JdbcType.DATETIME){
			if("date".equals(column.columnDefinition())){
				jdbcType = JdbcType.DATE;
			}else if("time".equals(column.columnDefinition())){
				jdbcType = JdbcType.TIME;
			}
		}
		return new FieldStructure(column.name(), jdbcType);
	}
	
	private int getGenerateType(GenerationType generatedType) {
		int generateType = TableStructure.PK_NATIVE;
		if(generatedType == GenerationType.IDENTITY){
			return TableStructure.PK_NATIVE;
		}else if(generatedType == GenerationType.SEQUENCE){
			return TableStructure.PK_SEQUENCE;
		}else if(generatedType == GenerationType.AUTO){
			return TableStructure.PK_CUSTOMIZE;
		}
		return generateType;
	}
}
