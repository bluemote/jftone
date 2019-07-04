/**
 * Dao.java
 * 数据访问
 * 
 * @author		zhoubing
 * @date   		Jul 8, 2011
 * @revision	v1.0
 */
package org.jftone.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.jftone.config.Const;
import org.jftone.datasource.DBType;
import org.jftone.datasource.DataSourceUtil;
import org.jftone.datasource.RouteDataSource;
import org.jftone.exception.DaoException;
import org.jftone.jdbc.DBRepository;
import org.jftone.jdbc.DBUtil;
import org.jftone.jdbc.JdbcType;
import org.jftone.jdbc.SqlSort;
import org.jftone.jdbc.SqlStructure;
import org.jftone.jdbc.SqlStructureWrapper;
import org.jftone.jdbc.SqlWrapper;
import org.jftone.jdbc.TableStructure;
import org.jftone.jdbc.TableStructureWrapper;
import org.jftone.jdbc.handler.BeanHandler;
import org.jftone.jdbc.handler.BeanListHandler;
import org.jftone.jdbc.handler.MapListHandler;
import org.jftone.model.Model;
import org.jftone.util.EncryptUtil;
import org.jftone.util.IData;
import org.jftone.util.ObjectUtil;
import org.jftone.util.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Dao {
	private Logger log = LoggerFactory.getLogger(Dao.class);
	
	private RouteDataSource datasource;
	
	public void setDatasource(RouteDataSource datasource) {
		this.datasource = datasource;
	}

	/**
	 * 插入一条记录
	 * @param model
	 * @return
	 * @throws DaoException
	 */
	public <T extends Model> T save(T model) throws DaoException {
		String modelName = model.getClass().getSimpleName();
		TableStructure table = DBRepository.getTable(modelName);
		boolean flag = table.getGenerateType() == TableStructure.PK_NATIVE ? true : false;
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {
			//获取新增库表字段
			TableStructureWrapper tsWrapper = TableStructureWrapper.getWrapper(table);
			tsWrapper.parseInsertModel(model);
			List<String> insertFields = tsWrapper.getParseFields();
			List<Object> paramObjects = tsWrapper.getParseValues();
			List<JdbcType> paramTypes = tsWrapper.getParseTypes();
			//创建新增SQL语句
			String insertSQL = datasource.getSqlWrapper().buildInsertSQL(table.getName(), insertFields);
			Object returnValue = DBUtil.insert(conn, insertSQL, flag, paramObjects.toArray(), paramTypes.toArray(new JdbcType[0]));
			if(null != returnValue && flag){
				ObjectUtil.setProperty(model, 
						ObjectUtil.getPropertyName(table.getPrimaryKey()), 
						(table.getPrimaryType()).getJavaValue(String.valueOf(returnValue)));
			} 
		} catch (Exception e) {
			log.error(String.format("保存[%s]数据错误", modelName), e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return model;
	}
	
	/**
	 * 批处理新增
	 * @param models
	 * @return
	 * @throws DaoException
	 */
	public <T extends Model> boolean insertBatch(List<T> models) throws DaoException {
		boolean resultFlag = false;
		if(null == models || models.size() == 0){
			log.error("批处理新增参数对象为空");
			throw new DaoException("批处理新增参数对象为空");
		}
		T model = models.get(0);
		String modelName = model.getClass().getSimpleName();
		TableStructure table = DBRepository.getTable(modelName);
		List<String> sqlFields = null;
		List<JdbcType> types = null;
		List<Object[]> paramList = new ArrayList<Object[]>();
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {
			TableStructureWrapper tsWrapper = TableStructureWrapper.getWrapper(table);	
			int i=0;
			for(T obj : models){
				tsWrapper.parseInsertModel(obj);
				if(i==0){
					sqlFields = tsWrapper.getParseFields();
					types = tsWrapper.getParseTypes();
				}
				paramList.add(tsWrapper.getParseValues().toArray());
				i++;
			}
			//创建新增SQL语句
			String insertSQL = datasource.getSqlWrapper().buildInsertSQL(table.getName(), sqlFields);
			resultFlag = DBUtil.executeBatch(conn, insertSQL, paramList, types.toArray(new JdbcType[0]));
			
		} catch (Exception e) {
			log.error(String.format("批处理新增[%s]数据错误", modelName), e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return resultFlag ;
	}
	
	/**
	 *  根据主键修改对象
	 * @param model
	 * @return
	 * @throws DaoException
	 */
	public <T extends Model> int update(T model) throws DaoException {
		int result = 0 ;
		String modelName = model.getClass().getSimpleName();
		TableStructure table = DBRepository.getTable(modelName);
		Connection conn = null;
		try {
			Object id = ObjectUtil.getProperty(model, ObjectUtil.getPropertyName(table.getPrimaryKey()));
			if(null == id){
				throw new DaoException("更新对象对应库表主键字段值为空");
			}
			conn = DataSourceUtil.getConnection(datasource);
			TableStructureWrapper tsWrapper = TableStructureWrapper.getWrapper(table);	
			tsWrapper.parseNotNullModel(model);
			List<String> sqlFields = tsWrapper.getParseFields();
			List<Object> paramObjects = tsWrapper.getParseValues();
			List<JdbcType> paramTypes = tsWrapper.getParseTypes();
			
			//增加条件字段对象及类型
			paramObjects.add(id);
			paramTypes.add(table.getPrimaryType());
			
			String editSQL = datasource.getSqlWrapper().buildUpdateSQL(table.getName(), sqlFields, tsWrapper.getPkField());
			
			result = DBUtil.update(conn, editSQL, paramObjects.toArray(), paramTypes.toArray(new JdbcType[0]));
		} catch (Exception e) {
			log.error(String.format("修改[%s]数据错误", modelName), e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return result ;
	}
	
	/**
	 * 根据IData更新字段及查询字段修改modelCls对象
	 * @param modelCls
	 * @param updateData
	 * @param whereData
	 * @return
	 * @throws DaoException
	 */
	public <T extends Model> int update(Class<T> modelCls, IData<String, Object> updateData, IData<String, Object> whereData) throws DaoException {
		int result = 0 ;
		if(null == updateData || updateData.size()<1){
			throw new DaoException("更新对象不能为空");
		}
		String modelName = modelCls.getSimpleName();
		TableStructure table = DBRepository.getTable(modelName);
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {
			TableStructureWrapper tsWrapper = TableStructureWrapper.getWrapper(table);	
			tsWrapper.parseDataMap(updateData);
			List<String> updateFields = tsWrapper.getParseFields();
			List<Object> paramObjects = tsWrapper.getParseValues();
			List<JdbcType> paramTypes = tsWrapper.getParseTypes();
			
			//增加条件字段对象及类型
			List<String> whereFields = null;
			if(null != whereData && whereData.size()>0){
				tsWrapper.parseDataMap(whereData);
				whereFields = tsWrapper.getParseFields();
				paramObjects.addAll(tsWrapper.getParseValues());
				paramTypes.addAll(tsWrapper.getParseTypes());
			}
			
			String editSQL = datasource.getSqlWrapper().buildUpdateSQL(table.getName(), updateFields, whereFields);
			
			result = DBUtil.update(conn, editSQL, paramObjects.toArray(), paramTypes.toArray(new JdbcType[0]));
		} catch (Exception e) {
			log.error(String.format("修改[%s]数据错误", modelName), e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return result ;
	}
	
	/**
	 * 批处理修改对象
	 * @param models
	 * @return
	 * @throws DaoException
	 */
	public <T extends Model> boolean updateBatch(List<T> models) throws DaoException {
		boolean result = false ;
		if(null == models || models.size() == 0){
			log.error("批处理修改参数对象为空");
			throw new DaoException("批处理修改参数对象为空");
		}
		String modelName = models.get(0).getClass().getSimpleName();
		TableStructure table = DBRepository.getTable(modelName);
		List<Object[]> paramList = new ArrayList<Object[]>();
		List<JdbcType> paramTypes = new ArrayList<JdbcType>();
		String editSQL = null;
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {
			TableStructureWrapper tsWrapper = TableStructureWrapper.getWrapper(table);	
			for(int i=0; i< models.size(); i++){
				T model = models.get(i);
				Object id = ObjectUtil.getProperty(model, ObjectUtil.getPropertyName(table.getPrimaryKey()));
				if(null == id){
					throw new DaoException("更新对象对应库表主键字段值为空");
				}
				tsWrapper.parseNotNullModel(model);
				List<Object> paramObjects = tsWrapper.getParseValues();
				//增加条件字段对象及类型
				paramObjects.add(id);
				paramList.add(paramObjects.toArray());
				if(i>0){
					continue;
				}
				List<String> sqlFields = tsWrapper.getParseFields();
				paramTypes = tsWrapper.getParseTypes();
				//增加主键类型
				paramTypes.add(table.getPrimaryType());
				editSQL = datasource.getSqlWrapper().buildUpdateSQL(table.getName(), sqlFields, tsWrapper.getPkField());
			}
			result = DBUtil.executeBatch(conn, editSQL, paramList, paramTypes.toArray(new JdbcType[0]));
		} catch (Exception e) {
			log.error(String.format("批处理修改[%s]数据错误", modelName), e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return result ;
	}
	
	/**
	 * 删除Model对象
	 * @param model
	 * @return
	 * @throws DaoException
	 */
	public <T extends Model> int delete(T model) throws DaoException {
		int result = 0 ;
		String modelName = model.getClass().getSimpleName();
		TableStructure table = DBRepository.getTable(modelName);
		List<String> sqlConds = null;
		List<Object> paramObjects = new ArrayList<Object>();
		List<JdbcType> paramTypes = new ArrayList<JdbcType>();
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {
			TableStructureWrapper tsWrapper = TableStructureWrapper.getWrapper(table);
			Object id = ObjectUtil.getProperty(model, ObjectUtil.getPropertyName(table.getPrimaryKey()));
			if(null != id){
				sqlConds = tsWrapper.getPkField();
				paramObjects.add(id);
				paramTypes.add(table.getPrimaryType());
			}else{
				tsWrapper.parseNotNullModel(model);
				sqlConds = tsWrapper.getParseFields();
				paramObjects = tsWrapper.getParseValues();
				paramTypes = tsWrapper.getParseTypes();
			}
			String delSQL = datasource.getSqlWrapper().buildDeleteSQL(table.getName() , sqlConds);
			result = DBUtil.update(conn, delSQL, paramObjects.toArray(), paramTypes.toArray(new JdbcType[0]));
			
		} catch (Exception e) {
			log.error(String.format("删除[%s]数据错误", modelName), e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return result ;
	}
	
	/**
	 * 根据IData条件查询数据，删除对象
	 * @param model
	 * @return
	 * @throws DaoException
	 */
	public <T extends Model> int delete(Class<T> modelCls, IData<String, Object> whereData) throws DaoException {
		int result = 0 ;
		String modelName = modelCls.getSimpleName();
		TableStructure table = DBRepository.getTable(modelName);
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {
			TableStructureWrapper tsWrapper = TableStructureWrapper.getWrapper(table);
			List<String> condFields = null;
			List<Object> paramList = null;
			List<JdbcType> typeList = null;
			
			if(null != whereData && whereData.size()>0){
				tsWrapper.parseDataMap(whereData);
				condFields = tsWrapper.getParseFields();
				paramList = tsWrapper.getParseValues();
				typeList = tsWrapper.getParseTypes();
			}
			
			String delSQL = datasource.getSqlWrapper().buildDeleteSQL(table.getName() , condFields);
			result = DBUtil.update(conn, delSQL, paramList==null? null : paramList.toArray(), typeList==null? null : typeList.toArray(new JdbcType[0]));
			
		} catch (Exception e) {
			log.error(String.format("删除[%s]数据错误", modelName), e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return result ;
	}
	
	/**
	 * 批处理删除对象
	 * @param models
	 * @return
	 * @throws DaoException
	 */
	public <T extends Model> boolean delBatch(List<T> models) throws DaoException {
		boolean result = false ;
		if(null == models || models.size() == 0){
			log.error("批处理删除参数对象为空");
			throw new DaoException("批处理删除参数对象为空");
		}
		T model = models.get(0);
		String modelName = model.getClass().getSimpleName();
		TableStructure table = DBRepository.getTable(modelName);
		List<Object[]> paramList = new ArrayList<Object[]>();
		List<JdbcType> paramTypes = new ArrayList<JdbcType>();
		String delSQL = null;
		List<String> sqlFields = null;
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {
			TableStructureWrapper tsWrapper = TableStructureWrapper.getWrapper(table);
			//确定删除是以主键还是其他字段
			boolean flag = ObjectUtil.getProperty(model, ObjectUtil.getPropertyName(table.getPrimaryKey())) == null? false : true;
			for(int i=0; i< models.size(); i++){
				T obj = models.get(i);
				if(flag){
					paramList.add(new Object[]{ObjectUtil.getProperty(obj, ObjectUtil.getPropertyName(table.getPrimaryKey()))});
					if(i>0){
						continue;
					}
					sqlFields = tsWrapper.getPkField();
					paramTypes.add(table.getPrimaryType());
				}else{
					tsWrapper.parseNotNullModel(obj);
					paramList.add((tsWrapper.getParseValues()).toArray());
					if(i>0){
						continue;
					}
					sqlFields = tsWrapper.getParseFields();
					paramTypes = tsWrapper.getParseTypes();	
				}
			}
			delSQL = datasource.getSqlWrapper().buildDeleteSQL(table.getName(), sqlFields);
			result = DBUtil.executeBatch(conn, delSQL, paramList, paramTypes.toArray(new JdbcType[0]));
		} catch (Exception e) {
			log.error(String.format("批处理删除[%s]数据错误", modelName), e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return result ;
	}
	
	/**
	 * 根据主键获取对象
	 * @param modelCls
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public <T extends Model> T get(Class<T> modelCls, Object id) throws DaoException {
		T model = null;
		String modelName = modelCls.getSimpleName();
		TableStructure table = DBRepository.getTable(modelName);
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {
			TableStructureWrapper tsWrapper = TableStructureWrapper.getWrapper(table);	
			List<String> sqlConds = tsWrapper.getPkField();
			List<String> queryFields = tsWrapper.getAllField();
			String loadSQL = datasource.getSqlWrapper().buildSelectSQL(table.getName(), queryFields, sqlConds, null);
			
			model = modelCls.newInstance();
			model = DBUtil.query(conn, loadSQL, new Object[]{id}, new JdbcType[]{table.getPrimaryType()}, 
					new BeanHandler<T>(modelCls, table.getFieldData()));
		} catch (Exception e) {
			log.error(String.format("查询[%s]数据错误", modelCls.getName()), e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return model;
	}
	public <T extends Model> T get(final Class<T> modelCls, IData<String, Object> whereData) throws DaoException {
		List<T> list = query(modelCls, whereData);
		if(null != list && list.size()>1) {
			throw new DaoException("查询结果返回多行记录数据");
		}
		return (null==list || list.size()==0)? null : list.get(0);
	}
	
	/**
	 * 根据过滤条件查询多个对象
	 * @param <T>
	 * @param modelCls		查询对象
	 * @param whereData		查询条件
	 * @return
	 * @throws DaoException
	 */
	public <T extends Model> List<T> query(final Class<T> modelCls, IData<String, Object> whereData) throws DaoException {
		return query(modelCls, whereData, null, null);
	}
	/**
	 * 根据过滤条件查询多个对象
	 * @param <T>
	 * @param modelCls		查询对象
	 * @param whereData		查询条件
	 * @param sqlOrderList	排序
	 * @return
	 * @throws DaoException
	 */
	public <T extends Model> List<T> query(final Class<T> modelCls, IData<String, Object> whereData, List<DataSort> dataSortList) throws DaoException {
		return query(modelCls, whereData, dataSortList, null);
	}
	
	/**
	 * 根据过滤条件返回一定数量的对象
	 * 分页使用
	 * @param model		查询对象
	 * @param whereData	查询条件
	 * @param page		分页对象
	 * @return
	 * @throws DaoException
	 */
	public <T extends Model> List<T> query(final Class<T> modelCls, IData<String, Object> whereData, Page page) throws DaoException {
		return query(modelCls, whereData, null, page) ;
	}
	/**
	 * 根据过滤条件查询多个对象
	 * @param <T>
	 * @param modelCls		查询对象
	 * @param whereData		查询条件
	 * @param sqlOrderList	排序
	 * @param page			分页
	 * @return
	 * @throws DaoException
	 */
	public <T extends Model> List<T> query(final Class<T> modelCls, IData<String, Object> whereData, List<DataSort> dataSortList, Page page) throws DaoException {
		List<T> resultList = new ArrayList<T>();
		String modelName = modelCls.getSimpleName();
		TableStructure table = DBRepository.getTable(modelName);
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {
			TableStructureWrapper tsWrapper = TableStructureWrapper.getWrapper(table);
			List<String> selectFields = tsWrapper.getAllField();
			List<String> condFields = null;
			List<Object> paramList = null;
			List<JdbcType> typeList = null;
			
			if(null != whereData && whereData.size()>0){
				tsWrapper.parseDataMap(whereData);
				condFields = tsWrapper.getParseFields();
				paramList = tsWrapper.getParseValues();
				typeList = tsWrapper.getParseTypes();
			}
			
			Object[] params = paramList==null? null : paramList.toArray();
			JdbcType[] types = typeList==null? null : typeList.toArray(new JdbcType[0]);
			//排序字段
			List<SqlSort> sqlSortList = null == dataSortList ? null : tsWrapper.parseSortList(dataSortList);
			if(null == page) {
				String loadSQL = datasource.getSqlWrapper().buildSelectSQL(table.getName(), tsWrapper.getAllField(), condFields, sqlSortList);
				resultList = DBUtil.query(conn, loadSQL, params, types, new BeanListHandler<T>(modelCls, table.getFieldData()));
			}else {
				SqlWrapper sqlWrapper = datasource.getSqlWrapper();
				String countSQL = sqlWrapper.buildCountSQL(table.getName(), condFields);
				long resultCount = DBUtil.count(conn, countSQL, params , types);
				if(resultCount == 0){
					return resultList;
				}
				page.setRecordCount(resultCount);
				String loadPageSQL = sqlWrapper.buildSelectSQL(table.getName(), selectFields, condFields, sqlSortList, page.getStart(), page.getPageSize());
				resultList = DBUtil.query(conn , loadPageSQL, params , types, new BeanListHandler<T>(modelCls, table.getFieldData()));			
			}
		} catch (Exception e) {
			log.error(String.format("查询[%s]分页数据错误", modelCls.getName()), e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return resultList ;
	}
	
	/**
	 * 根据过滤条件统计符合要求的记录数
	 * @param model
	 * @return
	 * @throws DaoException
	 */
	public <T extends Model> long count(Class<T> modelCls, IData<String, Object> whereData) throws DaoException {
		long resultCount = 0;
		String modelName = modelCls.getSimpleName();
		TableStructure table = DBRepository.getTable(modelName);
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {	
			List<String> condFields = null;//解析后直接执行的SQL
			List<Object> paramList = null;					//SQL中的入参查询字段
			List<JdbcType> typeList = null;		//SQL中入参查询字段数据类型
		
			if(null != whereData && whereData.size()>0){
				TableStructureWrapper tsWrapper = TableStructureWrapper.getWrapper(table);
				tsWrapper.parseDataMap(whereData);
				condFields = tsWrapper.getParseFields();
				paramList = tsWrapper.getParseValues();
				typeList = tsWrapper.getParseTypes();
			}
			
			String countSQL = datasource.getSqlWrapper().buildCountSQL(table.getName(), condFields);
			resultCount = DBUtil.count(conn, countSQL, paramList==null? null : paramList.toArray() , typeList==null? null : typeList.toArray(new JdbcType[0]));
			
		} catch (Exception e) {
			log.error(String.format("统计[%s]数据错误", modelName), e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return resultCount;
	}
	
	/**
	 * 根据SQL配置查询多条数据
	 * @param statementName
	 * @param paramData
	 * @return
	 * @throws DaoException
	 */
	public <T extends Model> List<T> query(String statementName, Class<T> modelCls, IData<String, Object> paramData) throws DaoException {
		return dbQuery(modelCls, statementName, paramData, null);
	}
	public List<IData<String, Object>> query(String statementName, IData<String, Object> paramData) throws DaoException {
		return dbQuery(statementName, paramData, null);
	}

	/**
	 * 根据查询sql进行联合查询，默认不进行重复筛选
	 * @param statementName
	 * @param paramDataList<IData<String, Object>>
	 * @return
	 * @throws DaoException
	 */
	public <T extends Model> List<T> queryUnion(String statementName, Class<T> modelCls, List<IData<String, Object>> paramDataList) throws DaoException {
		return queryUnion(statementName, modelCls, paramDataList, true);
	}
	public List<IData<String, Object>> queryUnion(String statementName, List<IData<String, Object>> paramDataList) throws DaoException {
		return queryUnion(statementName, paramDataList, true);
	}
	/**
	 * 根据查询sql进行联合查询
	 * @param statementName
	 * @param paramDataList
	 * @param unionAll		
	 * @return
	 * @throws DaoException
	 */
	public <T extends Model> List<T> queryUnion(String statementName, Class<T> modelCls, List<IData<String, Object>> paramDataList, boolean unionAll) throws DaoException {
		return dbQueryUnion(statementName, paramDataList, unionAll, modelCls) ;
	}
	public List<IData<String, Object>> queryUnion(String statementName, List<IData<String, Object>> paramDataList, boolean unionAll) throws DaoException {
		return dbQueryUnion(statementName, paramDataList, unionAll);
	}
	
	/**
	 * 分页查询
	 * @param statementName
	 * @param paramData
	 * @param page
	 * @return
	 * @throws DaoException
	 */
	public <T extends Model> List<T> query(String statementName, Class<T> modelCls, IData<String, Object> paramData, Page page) throws DaoException {
		return dbQuery(modelCls, statementName, paramData, page);
	}
	public List<IData<String, Object>> query(String statementName, IData<String, Object> paramData, Page page) throws DaoException {
		return dbQuery(statementName, paramData, page);
	}
	private <T extends Model> List<T> dbQueryUnion(String statementName, List<IData<String, Object>> paramDataList, boolean unionAll, Class<T> retCls) throws DaoException{
		List<T> retList = new ArrayList<T>();
		SqlStructure sqlObj = DBRepository.getSQLStatement(statementName);
		if(null == sqlObj){
			throw new DaoException("无法获取标识为："+statementName+"的SQL配置");
		}
		if(null == paramDataList || paramDataList.size() ==0){
			throw new DaoException("标识为："+statementName+"的SQL无入参数据");
		}
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {
			String unionStr = unionAll? " union all " : " union ";
			SqlStructureWrapper sWrapper = SqlStructureWrapper.getWrapper(sqlObj);
			
			StringBuilder sqlBuilder = new StringBuilder();
			List<Object> paramList = new ArrayList<Object>();
			List<JdbcType> typeList = new ArrayList<JdbcType>();
			
			int i=0;
			String tmpSql = "";
			List<JdbcType> tmpTypes = null;
			
			DBType dbType = datasource.getDBType();
			
			for(IData<String, Object> paramData : paramDataList){
				sWrapper.parseParam(paramData);
				paramList.addAll(sWrapper.getParamValues());
				if(i<=0) {
					tmpSql = sWrapper.getSqlSentence();
					tmpTypes = sWrapper.getParamTypes();
				}else{
					sqlBuilder.append(unionStr);
				}
				typeList.addAll(tmpTypes);
				//对于非sqlite数据库，增加括号
				sqlBuilder.append(dbType.code().equals(DBType.SQLITE.code())? tmpSql : "(" + tmpSql + ")");
				i++;
			}
			
			String sql = paramDataList.size() == 1 ? tmpSql : sqlBuilder.toString();
			Object[] params = (paramList==null || paramList.size()==0)? null : paramList.toArray();
			JdbcType[] types = (typeList==null || typeList.size()==0)? null : typeList.toArray(new JdbcType[0]);
			retList = DBUtil.query(conn, sql, params, types, new BeanListHandler<T>(retCls));
		} catch (Exception e) {
			log.error("标识为："+statementName+"的Union SQL语句,查询数据错误", e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return retList;
	}
	private List<IData<String, Object>> dbQueryUnion(String statementName, List<IData<String, Object>> paramDataList, boolean unionAll) throws DaoException{
		List<IData<String, Object>> retList = new ArrayList<IData<String, Object>>();
		SqlStructure sqlObj = DBRepository.getSQLStatement(statementName);
		if(null == sqlObj){
			throw new DaoException("无法获取标识为："+statementName+"的SQL配置");
		}
		if(null == paramDataList || paramDataList.size() ==0){
			throw new DaoException("标识为："+statementName+"的SQL无入参数据");
		}
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {
			String unionStr = unionAll? " union all " : " union ";
			SqlStructureWrapper sWrapper = SqlStructureWrapper.getWrapper(sqlObj);
			
			StringBuilder sqlBuilder = new StringBuilder();
			List<Object> paramList = new ArrayList<Object>();
			List<JdbcType> typeList = new ArrayList<JdbcType>();
			
			int i=0;
			String tmpSql = "";
			List<JdbcType> tmpTypes = null;
			
			for(IData<String, Object> paramData : paramDataList){
				sWrapper.parseParam(paramData);
				paramList.addAll(sWrapper.getParamValues());
				if(i<=0) {
					tmpSql = sWrapper.getSqlSentence();
					tmpTypes = sWrapper.getParamTypes();
				}else{
					sqlBuilder.append(unionStr);
				}
				typeList.addAll(tmpTypes);
				sqlBuilder.append(tmpSql);
				i++;
			}
			
			String sql = paramDataList.size() == 1 ? tmpSql : sqlBuilder.toString();
			Object[] params = (paramList==null || paramList.size()==0)? null  :  paramList.toArray();
			JdbcType[] types = (typeList==null || typeList.size()==0)? null : typeList.toArray(new JdbcType[0]);
			retList = DBUtil.query(conn, sql, params, types, new MapListHandler());
		} catch (Exception e) {
			log.error("标识为："+statementName+"的Union SQL语句,查询数据错误", e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return retList;
	}
	private List<IData<String, Object>> dbQuery(String statementName, IData<String, Object> paramData, Page page) throws DaoException{
		List<IData<String, Object>> retList = new ArrayList<IData<String, Object>>();
		SqlStructure sqlObj = DBRepository.getSQLStatement(statementName);
		if(null == sqlObj){
			throw new DaoException("无法获取标识为："+statementName+"的SQL配置");
		}
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {
			SqlStructureWrapper sWrapper = SqlStructureWrapper.getWrapper(sqlObj);
			sWrapper.parseParam(paramData);
			List<Object> paramList = sWrapper.getParamValues();
			List<JdbcType> typeList = sWrapper.getParamTypes();
			String sql = sWrapper.getSqlSentence();
			Object[] params = (paramList==null || paramList.size()==0)? null  :  paramList.toArray();
			JdbcType[] types = (typeList==null || typeList.size()==0)? null : typeList.toArray(new JdbcType[0]);
			
			if(page != null){
				long resultCount = DBUtil.count(conn, "select count(*) from ("+sql+") JFT", params, types);
				if(resultCount == 0){
					return retList;
				}
				page.setRecordCount(resultCount);
				sql += " LIMIT "+page.getStart()+Const.SPLIT_COMMA+page.getPageSize();
			}
			retList = DBUtil.query(conn, sql, params, types, new MapListHandler());
			
		} catch (Exception e) {
			log.error("标识为："+statementName+"的SQL语句,查询数据错误", e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return retList;
	}
	private <T extends Model> List<T> dbQuery(Class<T> retCls, String statementName, IData<String, Object> paramData, Page page) throws DaoException{
		List<T> retList = new ArrayList<T>();
		SqlStructure sqlObj = DBRepository.getSQLStatement(statementName);
		if(null == sqlObj){
			throw new DaoException("无法获取标识为："+statementName+"的SQL配置");
		}
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {
			SqlStructureWrapper sWrapper = SqlStructureWrapper.getWrapper(sqlObj);
			sWrapper.parseParam(paramData);
			List<Object> paramList = sWrapper.getParamValues();
			List<JdbcType> typeList = sWrapper.getParamTypes();
			String sql = sWrapper.getSqlSentence();
			Object[] params = (paramList==null || paramList.size()==0)? null  :  paramList.toArray();
			JdbcType[] types = (typeList==null || typeList.size()==0)? null : typeList.toArray(new JdbcType[0]);
			
			if(page != null){
				long resultCount = DBUtil.count(conn, "select count(*) from ("+sql+") JFT", params, types);
				if(resultCount == 0){
					return retList;
				}
				page.setRecordCount(resultCount);
				sql += " LIMIT "+page.getStart()+Const.SPLIT_COMMA+page.getPageSize();
			}
			retList = DBUtil.query(conn, sql, params, types, new BeanListHandler<T>(retCls));
		} catch (Exception e) {
			log.error("标识为："+statementName+"的SQL语句,查询数据错误", e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return retList;
	}
	
	/**
	 * 根据SQL配置获取某条记录
	 * @param statementName
	 * @param paramData
	 * @return
	 * @throws DaoException
	 */
	public <T extends Model> T get(String statementName, Class<T> modelCls, IData<String, Object> paramData) throws DaoException {
		List<T> list = this.query(statementName, modelCls, paramData);
		if(null != list && list.size()>1) {
			throw new DaoException("查询结果返回多行记录数据");
		}
		return (null==list || list.size()==0)? null : list.get(0);
	}
	public IData<String, Object> get(String statementName, IData<String, Object> paramData) throws DaoException {
		List<IData<String, Object>> list = this.query(statementName, paramData);
		if(null != list && list.size()>1) {
			throw new DaoException("查询结果返回多行记录数据");
		}
		return (null==list || list.size()==0)? null : list.get(0);
	}
	
	/**
	 * 根据SQL配置更新某条数据
	 * @param statementName
	 * @param paramData
	 * @return
	 * @throws DaoException
	 */
	public int update(String statementName, IData<String, Object> paramData) throws DaoException {
		int result = 0;
		SqlStructure sqlObj = DBRepository.getSQLStatement(statementName);
		if(null == sqlObj){
			throw new DaoException("无法获取标识为："+statementName+"的SQL配置");
		}
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {
			SqlStructureWrapper sWrapper = SqlStructureWrapper.getWrapper(sqlObj);
			sWrapper.parseParam(paramData);
			List<Object> paramList = sWrapper.getParamValues();
			List<JdbcType> typeList = sWrapper.getParamTypes();
			String sql = sWrapper.getSqlSentence();
			Object[] params = (paramList==null || paramList.size()==0)? null  :  paramList.toArray();
			JdbcType[] types = (typeList==null || typeList.size()==0)? null : typeList.toArray(new JdbcType[0]);
			result = DBUtil.update(conn, sql, params, types);
			
		} catch (Exception e) {
			log.error("标识为："+statementName+"的SQL语句,更新数据错误", e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return result ;
	}
	
	/**
	 * 根据SQL配置批处理更新数据
	 * @param statementName
	 * @param paramDataList
	 * @return
	 * @throws DaoException
	 */
	public boolean updateBatch(String statementName, List<IData<String, Object>> paramDataList) throws DaoException {
		boolean result = false;
		SqlStructure sqlObj = DBRepository.getSQLStatement(statementName);
		if(null == sqlObj){
			throw new DaoException("无法获取标识为："+statementName+"的SQL配置");
		}
		if(null == paramDataList || paramDataList.size() ==0){
			throw new DaoException("标识为："+statementName+"的SQL无入参数据");
		}
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {
			SqlStructureWrapper sWrapper = SqlStructureWrapper.getWrapper(sqlObj);
			
			String sql = "";
			List<Object[]> paramList = new ArrayList<Object[]>();
			List<JdbcType> typeList = null;
			
			int i=0;
			List<Object> params = null;
			for(IData<String, Object> paramData : paramDataList){
				sWrapper.parseParam(paramData);
				params = sWrapper.getParamValues();
				paramList.add(params.toArray());
				if(i>0) {
					continue;
				}
				sql = sWrapper.getSqlSentence();
				typeList = sWrapper.getParamTypes();
				i++;
			}
			JdbcType[] types = (typeList==null || typeList.size()==0)? null : typeList.toArray(new JdbcType[0]);
			result = DBUtil.executeBatch(conn, sql, paramList, types);
		} catch (Exception e) {
			log.error("标识为："+statementName+"的SQL语句,批量更新数据错误", e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return result ;
	}
	
	public boolean executeBySql(String sql) throws DaoException {
		boolean result = true;
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {
			result = DBUtil.execute(conn, sql);
		} catch (Exception e) {
			log.error("SQL["+sql+"],更新数据错误", e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return result ;
	}
	
	public List<IData<String, Object>> queryBySql(String sql, IData<String, Object> paramData) throws DaoException{
		List<IData<String, Object>> retList = new ArrayList<IData<String, Object>>();
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {
			SqlStructure sqlObj = new SqlStructure(EncryptUtil.md5(sql));
			sqlObj.setSql(sql);
			SqlStructureWrapper sWrapper = SqlStructureWrapper.getWrapper(sqlObj);
			sWrapper.parseParam(paramData);
			List<Object> paramList = sWrapper.getParamValues();
			List<JdbcType> typeList = sWrapper.getParamTypes();
			String tmpSql = sWrapper.getSqlSentence();
			Object[] params = (paramList==null || paramList.size()==0)? null  :  paramList.toArray();
			JdbcType[] types = (typeList==null || typeList.size()==0)? null : typeList.toArray(new JdbcType[0]);
			retList = DBUtil.query(conn, tmpSql, params, types, new MapListHandler());
		} catch (Exception e) {
			log.error("SQL["+sql+"],查询数据错误", e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return retList;
	}
	
	public boolean existTable(String tableName) throws DaoException {
		boolean falg = false;
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {
			falg = DBUtil.existTable(conn, tableName);
			
		} catch (Exception e) {
			log.error("查询表["+tableName+"]是否存在错误", e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return falg ;
	}
	
	public boolean executeCall(String statementName, IData<String, Object> paramData) throws DaoException {
		boolean falg = false;
		SqlStructure sqlObj = DBRepository.getSQLStatement(statementName);
		Connection conn = DataSourceUtil.getConnection(datasource);
		try {
			if(null == sqlObj){
				throw new DaoException("无法获取标识为："+statementName+"的SQL配置");
			}
			SqlStructureWrapper sWrapper = SqlStructureWrapper.getWrapper(sqlObj);
			sWrapper.parseParam(paramData);
			List<Object> paramList = sWrapper.getParamValues();
			List<JdbcType> typeList = sWrapper.getParamTypes();
			String sql = sWrapper.getSqlSentence();
			Object[] params = (paramList==null || paramList.size()==0)? null  :  paramList.toArray();
			JdbcType[] types = (typeList==null || typeList.size()==0)? null : typeList.toArray(new JdbcType[0]);
			falg = DBUtil.call(conn, sql, params, types);
			
		} catch (Exception e) {
			log.error("标识为："+statementName+"的SQL语句,更新数据错误", e);
			throw new DaoException(e);
		}finally{
			DataSourceUtil.releaseConnection(conn, datasource);
		}
		return falg ;
	}
}
