package org.jftone.transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class TransactionStatus {
	private Class<?> interceptorClass;
	private String interceptorName;
	private Set<String> dataSourceKeys;
	private Map<String, Boolean> newTxMap; 
	private Map<String, Boolean> readOnlyMap;
	private Map<String, Boolean> suspendMap;
	
	public TransactionStatus(Class<?> serviceCls,String interceptorName){
		this.interceptorClass = serviceCls;
		this.interceptorName = interceptorName;
	}
	
	public Set<String> getDataSourceKey(){
		return this.dataSourceKeys;
	}
	public void setDataSourceKey(Set<String> dataSourceKeys){
		this.dataSourceKeys = dataSourceKeys;
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
	public boolean isNewTransaction(){
		if(empty()) {
			return false;
		}
		boolean flag = false;
		for(String key : dataSourceKeys){
			flag = (null != newTxMap && newTxMap.containsKey(key)) ? newTxMap.get(key) : false;
			if(flag){
				return true;
			}
		}
		return false;
	}
	public boolean empty(){
		return (null == dataSourceKeys)? true : false;
	}
	
	public String toString(){
		return interceptorClass.getSimpleName()+"."+interceptorName;
	}
}
