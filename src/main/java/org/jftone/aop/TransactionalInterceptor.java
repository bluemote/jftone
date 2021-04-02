package org.jftone.aop;

import java.lang.reflect.Method;
import java.sql.SQLException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jftone.exception.ServiceException;
import org.jftone.transaction.TransactionManager;
import org.jftone.transaction.TransactionStatus;

public final class TransactionalInterceptor implements AopInterceptor {
	private Logger log = LoggerFactory.getLogger(TransactionalInterceptor.class);
	
	private Class<?> beanClass;
	
	private Method method;
	
	public TransactionalInterceptor(Class<?> beanClass, Method method) {
		this.beanClass = beanClass;
		this.method = method;
	}
	
	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		Object retVal = null;
		TransactionStatus txStatus = null;
		try {
			txStatus = TransactionManager.getTransaction(this.beanClass, this.method);
			//调用具体业务方法
			retVal = methodInvocation.proceed();
			
			TransactionManager.commit(txStatus);
		} catch (Throwable e) {
			try {
				TransactionManager.rollback(txStatus, e);
			} catch (SQLException ex) {
				log.error("事务回滚错误", e);
			}
			throw new ServiceException(e);
		} finally { 
			TransactionManager.releaseResource(txStatus);
			txStatus = null;
        }
		return retVal;
	}
}
