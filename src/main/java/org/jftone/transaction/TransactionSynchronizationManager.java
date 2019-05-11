package org.jftone.transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.jftone.datasource.ConnectionHolder;

public class TransactionSynchronizationManager {

	//记录所有事务数据源键值对应的数据库连接持有对象[路由KEY=>Connection]
	private static final ThreadLocal<Map<String, ConnectionHolder>> connHolderLocal = new ThreadLocal<>();
	//记录所有开启事务对应的路由KEY[路由KEY]
	private static final ThreadLocal<Stack<String>> txStackLocal = new ThreadLocal<>();

	/**
	 * 数据库连接对象
	 * 
	 * @param key
	 * @return
	 */
	public static ConnectionHolder getConnectionHolder(String key) {
		Map<String, ConnectionHolder> map = connHolderLocal.get();
		if (null == map) {
			return null;
		}
		return map.get(key);
	}

	public static Map<String, ConnectionHolder> getConnectionHolderMap() {
		return connHolderLocal.get();
	}
	public static void clearConnectionHolder(String key) {
		connHolderLocal.get().remove(key);
	}

	public static void setConnectionHolder(String key, ConnectionHolder connectionHolder) {
		Map<String, ConnectionHolder> map = connHolderLocal.get();
		if (null == map) {
			map = new HashMap<>();
			connHolderLocal.set(map);
		}
		map.put(key, connectionHolder);
	}

	/**
	 * 数据是否开启事务
	 * 
	 * @param key
	 * @return
	 */
	public static boolean isTransactionActive(String key) {
		Stack<String> stack = txStackLocal.get();
		return (null != stack && stack.contains(key)) ? true : false;
	}

	public static Stack<String> getTxStack() {
		return txStackLocal.get();
	}

	public static void pushTxStack(String key) {
		Stack<String> stack = txStackLocal.get();
		if (null == stack) {
			stack = new Stack<>();
			txStackLocal.set(stack);
		} else {
			if (stack.contains(key))
				return;
		}
		stack.push(key);
	}

	public static boolean isTopTransation(Set<String> keys) {
		Stack<String> stack = txStackLocal.get();
		if (null == stack || stack.isEmpty())
			return true;
		if (keys.contains(stack.firstElement())) {
			return true;
		}
		return false;
	}

	public static void clear() {
		connHolderLocal.remove();
		txStackLocal.remove();
	}
}
