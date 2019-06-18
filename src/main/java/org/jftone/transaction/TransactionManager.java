package org.jftone.transaction;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jftone.annotation.Transactional;
import org.jftone.aop.AopUtil;
import org.jftone.aop.TransactionalPointcut;
import org.jftone.component.AspectContext;
import org.jftone.config.PropertyConfigurer;
import org.jftone.datasource.ConnectionHolder;
import org.jftone.datasource.DataSourceContext;
import org.jftone.datasource.DataSourceSynchronizationManager;
import org.jftone.datasource.DataSourceUtil;
import org.jftone.util.StringUtil;

public class TransactionManager {
	private static Log log = LogFactory.getLog(TransactionManager.class);

	private TransactionManager() {
		super();
	}

	/**
	 * 创建事务切面
	 */
	public static void createTransactionalPointcut() {
		TransactionalPointcut tp = new TransactionalPointcut();
		String transactional = PropertyConfigurer.get(PropertyConfigurer.TRANSACTIONAL);
		tp.setEnabled(transactional.equals("true"));

		String className = PropertyConfigurer.get(PropertyConfigurer.TRANSACTION_CLASS, "");
		if (!StringUtil.isBlank(className)) {
			tp.setClassNameExpression(AopUtil.parseClassName("Transaction", className));
			tp.setAllowAnyClass(AopUtil.allowAll(className)); // 是否允许所有代理类
		}
		String methodName = PropertyConfigurer.get(PropertyConfigurer.TRANSACTION_METHOD, "");
		if (!StringUtil.isBlank(methodName)) {
			tp.setMethodExpression(AopUtil.parseMethodName("Transaction", methodName));
			tp.setAllowAnyMethod(AopUtil.allowAll(methodName)); // 是否允许所有代理类方法
		}
		AspectContext.setTransactionalPointcut(tp);
	}

	/**
	 * 是否存在事务
	 * 
	 * @param key
	 * @return
	 */
	@Deprecated
	public static boolean isExistingTransaction(String key) {
		return TransactionSynchronizationManager.isTransactionActive(key);
	}

	/**
	 * 获取事务并打开，否则返回
	 * 
	 * @param targetClass
	 * @param method
	 * @return
	 * @throws SQLException
	 */
	public static TransactionStatus getTransaction(Class<?> targetClass, Method method) throws SQLException {
		String methodName = method.getName();
		TransactionalPointcut tp = AspectContext.getTransactionalPointcut();
		String[] dsNames = null;
		// 是否启用事务注解
		if (tp.isAnnotationTransactional(targetClass, method)) {
			// 如果启用事务注解，则从事务注解上获取启用事务注解的数据源映射KEY
			Transactional transactional = method.isAnnotationPresent(Transactional.class)
					? method.getAnnotation(Transactional.class) : targetClass.getAnnotation(Transactional.class);
			dsNames = transactional.value();
		}
		// 如果没有指定数据源，则获取默认数据源KEY设置为当前数据源KEY,如果是配置启用了集群，则为默认集群名字，如果环境负责，建议制定事务数据源或集群名字
		if (null == dsNames || dsNames.length == 0) {
			dsNames = new String[] { DataSourceContext.getDefaultRouteName() };
		}
		TransactionStatus txStatus = new TransactionStatus(targetClass, methodName);
		String routeKey;
		List<String> dsKeys = new ArrayList<>(dsNames.length);
		for (String keyName : dsNames) {
			routeKey = DataSourceSynchronizationManager.getDataSourceMappingKey(keyName, true); // 获取当前事务名称
			doGetTransaction(txStatus, routeKey);
			dsKeys.add(routeKey);
		}
		// 记录当前路由key
		txStatus.setTxRouteKey(new HashSet<>(dsKeys));
		return txStatus;
	}

	/**
	 * 开启具体的事务动作
	 * 
	 * @param txStatus
	 * @param key
	 * @param dataSourceName
	 * @return
	 * @throws SQLException
	 */
	protected static void doGetTransaction(TransactionStatus txStatus, String routeKey) throws SQLException {
		boolean txState = false;
		// 记录事务路由KEY,如果已经记录，则不压入堆栈
		TransactionSynchronizationManager.pushTxRouteHolder(routeKey);
		Connection conn = DataSourceUtil.getConnection(routeKey);
		// 如果没有开启事务，则开启
		if (conn.getAutoCommit()) {
			if (log.isDebugEnabled()) {
				log.debug("开启[" + txStatus + "]事务");
			}
			conn.setAutoCommit(false);
			txState = true;
		}
		// 记录当前事务状态，如果是嵌套事务，则状态为false
		txStatus.setNewTransaction(routeKey, txState);
	}

	/**
	 * 提交事物
	 * 
	 * @throws SQLException
	 */
	public static void commit(TransactionStatus txStatus) throws SQLException {
		if (null == txStatus || txStatus.empty()) {
			return;
		}
		// 必须是新事物，同时当前为栈顶事务（非栈顶事务表示是嵌套子事务，不做处理，递归到外层，由外层程序提交总事务）
		if (!txStatus.hasNewTransaction() || !TransactionSynchronizationManager
				.isTopTransation(txStatus.getTxRouteKey())) {
			return;
		}
		if (log.isDebugEnabled()) {
			log.debug("提交[" + txStatus + "]事务");
		}
		doCommit(txStatus);
	}

	private static void doCommit(TransactionStatus txStatus) throws SQLException {
		ConnectionHolder connHolder;
		Set<String> keys = txStatus.getTxRouteKey();
		for (String routeKey : keys) {
			if (!txStatus.isNewTransaction(routeKey)) {
				continue;
			}
			connHolder = TransactionSynchronizationManager.getConnectionHolder(routeKey);
			connHolder.getConnection().commit();
		}
	}

	/**
	 * 回滚事务
	 * 
	 * @throws SQLException
	 */
	public static void rollback(TransactionStatus txStatus, Throwable e) throws SQLException {
		if (null == txStatus || txStatus.empty()) {
			return;
		}
		if (!txStatus.hasNewTransaction() || !TransactionSynchronizationManager
				.isTopTransation(txStatus.getTxRouteKey())) {
			return;
		}
		if (log.isDebugEnabled()) {
			log.debug("回滚[" + txStatus + "]事务");
		}
		doRollback(txStatus);
	}

	private static void doRollback(TransactionStatus txStatus) throws SQLException {
		ConnectionHolder connHolder;
		Set<String> keys = txStatus.getTxRouteKey();
		for (String routeKey : keys) {
			if (!txStatus.isNewTransaction(routeKey)) {
				continue;
			}
			connHolder = TransactionSynchronizationManager.getConnectionHolder(routeKey);
			connHolder.getConnection().rollback();
		}
	}

	/**
	 * 释放资源
	 * 
	 * @param txStatus
	 */
	public static void releaseResource(TransactionStatus txStatus) {
		if (null == txStatus || txStatus.empty()) {
			return;
		} 
		doReleaseResource(txStatus);
	}

	private static void doReleaseResource(TransactionStatus txStatus) {
		try {
			ConnectionHolder connHolder;
			Set<String> keys = txStatus.getTxRouteKey();
			for (String key : keys) {
				connHolder = TransactionSynchronizationManager.getConnectionHolder(key);
				DataSourceUtil.releaseConnection(connHolder.getConnection(), key);
				if (txStatus.isNewTransaction(key) && 
						TransactionSynchronizationManager.isTopTransation(keys)) {
					TransactionSynchronizationManager.clearConnectionHolder(key);
					TransactionSynchronizationManager.clearTxRouteHolder(key);
					DataSourceSynchronizationManager.clearDataSourceName(key);
				}
			}
		} catch (Exception e) {
			log.debug("释放[" + txStatus + "]事务数据连接错误", e);
		}
	}
}
