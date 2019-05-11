package org.jftone.service;

import java.util.List;

import org.jftone.annotation.DataSource;
import org.jftone.annotation.Service;
import org.jftone.dao.Dao;
import org.jftone.dao.DaoContext;
import org.jftone.exception.DaoException;
import org.jftone.exception.ServiceException;
import org.jftone.model.Model;
import org.jftone.util.IData;
import org.jftone.util.Page;

/**
 * Service.java
 *
 * @author    zhoubing
 * @date      Mar 16, 2012
 * @revision  1.0
 */
@Deprecated
@Service
public class BaseService {
	/**
	 * 外部注入对象
	 */
	@DataSource
	protected Dao dao;			//默认数据源对应dao对象，如果多数据源，则配置文件中第一个
	
	/**
	 * BaseDAO数据访问层Delegate
	 * @param baseDao
	 */
	public void setDao(Dao dao) {
		this.dao = dao;
	}
	
	public Dao getDao(String dataSourceName) {
		return DaoContext.createDao(dataSourceName);
	}
	
	/**
	 * 保存实体对象数据
	 * @param model
	 * @return
	 * @throws ServiceException
	 */
	public <T extends Model> T save(T model) throws ServiceException {
		try {
			return dao.save(model);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 批处理新增
	 * @param models
	 * @return
	 * @throws ServiceException
	 */
	public <T extends Model> boolean insertBatch(List<T> models) throws ServiceException {
		try {
			return dao.insertBatch(models);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 更新对象
	 * @param model
	 * @return
	 * @throws ServiceException
	 */
	public <T extends Model> int update(T model) throws ServiceException {
		try {
			return dao.update(model);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 更新模型为modelCls的对象
	 * @param modelCls
	 * @param updateData
	 * @param whereData
	 * @return
	 * @throws ServiceException
	 */
	public <T extends Model> int update(Class<T> modelCls, IData<String, Object> updateData, IData<String, Object> whereData) throws ServiceException {
		try {
			return dao.update(modelCls, updateData, whereData);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 批处理修改对象
	 * @param models
	 * @return
	 * @throws ServiceException
	 */
	public <T extends Model> boolean updateBatch(List<T> models) throws ServiceException {
		try {
			return dao.updateBatch(models);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 删除对象
	 * @param model
	 * @return
	 * @throws ServiceException
	 */
	public <T extends Model> int delete(T model) throws ServiceException {
		try {
			return dao.delete(model);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 删除模型为modelCls的对象
	 * @param modelCls
	 * @param whereData
	 * @return
	 * @throws ServiceException
	 */
	public <T extends Model> int delete(Class<T> modelCls, IData<String, Object> whereData) throws ServiceException {
		try {
			return dao.delete(modelCls, whereData);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 批处理删除对象
	 * @param models
	 * @return
	 * @throws ServiceException
	 */
	public <T extends Model> boolean delBatch(List<T> models) throws ServiceException {
		try {
			return dao.delBatch(models);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 根据主键获取对象
	 * @param modelCls
	 * @param id	主键ID
	 * @return
	 * @throws ServiceException
	 */
	public <T extends Model> T get(Class<T> modelCls, Object id) throws ServiceException {
		try {
			return dao.get(modelCls, id);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 获取模型modelCls的实体对象
	 * @param modelCls
	 * @param whereData
	 * @return
	 * @throws ServiceException
	 */
	public <T extends Model> T get(Class<T> modelCls, IData<String, Object> whereData) throws ServiceException {
		try {
			return dao.get(modelCls, whereData);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 查询模型modelCls的实体对象
	 * @param model
	 * @throws ServiceException
	 */
	public <T extends Model> List<T> query(Class<T> modelCls, IData<String, Object> whereData) throws ServiceException {
		try {
			return dao.query(modelCls, whereData);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 查询模型modelCls的实体对象
	 * @param modelCls
	 * @return
	 * @throws ServiceException
	 */
	public <T extends Model> List<T> query(Class<T> modelCls) throws ServiceException {
		try {
			return dao.query(modelCls, null);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 分页查询模型modelCls对象
	 * 分页使用
	 * @param model
	 * @param page		分页对象
	 * @return
	 * @throws ServiceException
	 */
	public <T extends Model> List<T> query(Class<T> modelCls, IData<String, Object> whereData, Page page) throws ServiceException {
		try {
			return dao.query(modelCls, whereData, page);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 统计模型modelCls对象的记录数
	 * @param model
	 * @return
	 * @throws ServiceException
	 */
	public <T extends Model> long count(Class<T> modelCls, IData<String, Object> whereData) throws ServiceException {
		try {
			return dao.count(modelCls, whereData);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 根据SQL配置查询多条数据
	 * @param statementName
	 * @param paramData
	 * @return
	 * @throws ServiceException
	 */
	public <T extends Model> List<T> query(String statementName, Class<T> modelCls, IData<String, Object> paramData) throws ServiceException {
		try {
			return dao.query(statementName, modelCls, paramData);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	public List<IData<String, Object>> query(String statementName, IData<String, Object> paramData) throws ServiceException {
		try {
			return dao.query(statementName, paramData);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 分页查询
	 * @param statementName
	 * @param paramData
	 * @param page
	 * @return
	 * @throws ServiceException
	 */
	public <T extends Model> List<T> query(String statementName, Class<T> modelCls, IData<String, Object> paramData, Page page) throws ServiceException {
		try {
			return dao.query(statementName, modelCls, paramData, page);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	public List<IData<String, Object>> query(String statementName, IData<String, Object> paramData, Page page) throws ServiceException {
		try {
			return dao.query(statementName, paramData, page);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 根据表达式sql组装联合查询，默认返回全部，否则剔除冗余
	 * @param statementName
	 * @param paramList
	 * @param unionAll
	 * @return
	 * @throws ServiceException
	 */
	public <T extends Model> List<T> queryUnion(String statementName, Class<T> modelCls, List<IData<String, Object>> paramList, boolean unionAll) throws ServiceException {
		try {
			return dao.queryUnion(statementName, modelCls, paramList, unionAll);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	public List<IData<String, Object>> queryUnion(String statementName, List<IData<String, Object>> paramList, boolean unionAll) throws ServiceException {
		try {
			return dao.queryUnion(statementName, paramList, unionAll);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	public <T extends Model> List<T> queryUnion(String statementName, Class<T> modelCls, List<IData<String, Object>> paramList) throws ServiceException {
		try {
			return dao.queryUnion(statementName, modelCls, paramList);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	public List<IData<String, Object>> queryUnion(String statementName, List<IData<String, Object>> paramList) throws ServiceException {
		try {
			return dao.queryUnion(statementName, paramList);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 根据SQL配置获取某条记录
	 * @param statementName
	 * @param paramData
	 * @return
	 * @throws ServiceException
	 */
	public IData<String, Object> get(String statementName, IData<String, Object> paramData) throws ServiceException {
		try {
			return dao.get(statementName, paramData);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	public <T extends Model> T get(String statementName, Class<T> modelCls, IData<String, Object> paramData) throws ServiceException {
		try {
			return dao.get(statementName, modelCls, paramData);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 根据SQL配置更新某条数据
	 * @param statementName
	 * @param paramData
	 * @return
	 * @throws ServiceException
	 */
	public int update(String statementName, IData<String, Object> paramData) throws ServiceException {
		try {
			return dao.update(statementName, paramData);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 根据SQL配置批处理更新数据
	 * @param statementName
	 * @param paramList
	 * @return
	 * @throws ServiceException
	 */
	public boolean updateBatch(String statementName, List<IData<String, Object>> paramList) throws ServiceException {
		try {
			return dao.updateBatch(statementName, paramList);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
}
