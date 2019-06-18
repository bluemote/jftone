package org.jftone.transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class TransactionStatus {
	private Class<?> interceptorClass;
	private String interceptorName;
	private Set<String> txRouteKeys;			//存放当前事务拦截的数据源路由KEY
	private Map<String, Boolean> newTxMap; 		//记录当前事务拦截数据源路由KEY是否为新启用事务[true]，还是嵌套事务[false]
	private Map<String, Boolean> readOnlyMap;	//扩展预留
	private Map<String, Boolean> suspendMap;	//扩展预留
	
	public TransactionStatus(Class<?> serviceCls,String interceptorName){
		this.interceptorClass = serviceCls;
		this.interceptorName = interceptorName;
	}
	
	public Set<String> getTxRouteKey(){
		return this.txRouteKeys;
	}
	public void setTxRouteKey(Set<String> txRouteKeys){
		this.txRouteKeys = txRouteKeys;
	}
	
	public boolean isSuspend(String dataSourceKey){
		return suspendMap.get(dataSourceKey);
	}
	public void setSuspend(String dataSourceKey, boolean suspend){
		suspendMap.put(dataSourceKey, suspend);
	}
	
	public boolean isNewTransaction(String dataSourceKey){
		return (null != newTxMap && newTxMap.containsKey(dataSourceKey)) ? newTxMap.get(dataSourceKey) : false;
	}
	public void setNewTransaction(String dataSourceKey, boolean newTransaction){
		if(null == newTxMap){
			newTxMap = new HashMap<>();
		}
		newTxMap.put(dataSourceKey, newTransaction);
	}
	
	public boolean isReadOnly(String dataSourceKey){
		return readOnlyMap.get(dataSourceKey);
	}
	public void setReadOnly(String dataSourceKey, boolean readOnly){
		readOnlyMap.put(dataSourceKey, readOnly);
	}

	/**
	 * 同一个调用层级多个事务对象，只要其中有一个是新事务，则返回true,否则false
	 * @return
	 */
	public boolean hasNewTransaction(){
		if(empty()) {
			return false;
		}
		for(String key : txRouteKeys){
			if(isNewTransaction(key)){
				return true;
			}
		}
		return false;
	}
	public boolean empty(){
		return (null == txRouteKeys)? true : false;
	}
	
	public String toString(){
		return interceptorClass.getSimpleName()+"."+interceptorName;
	}
}
