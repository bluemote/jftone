/**
 * ActionMapping.java
 * Action路由映射对象
 * 
 * @author		zhoubing
 * @date   		Mar 29, 2012
 * @revision	v1.0
 */
package org.jftone.action;

import com.esotericsoftware.reflectasm.MethodAccess;

public class ActionMapping {
	private String actionKey;
	private Class<? extends Action> actionClass;
	private String methodName;
	private MethodAccess methodAccess;
	
	public ActionMapping(String actionKey, Class<? extends Action> actionClass,
			String methodName) {
		super();
		this.actionKey = actionKey;
		this.actionClass = actionClass;
		this.methodName = methodName;
		this.methodAccess = MethodAccess.get(actionClass);
	}
	
	public String getActionKey() {
		return actionKey;
	}
	public void setActionKey(String actionKey) {
		this.actionKey = actionKey;
	}
	public Class<? extends Action> getActionClass() {
		return actionClass;
	}
	public void setActionClass(Class<? extends Action> actionClass) {
		this.actionClass = actionClass;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public MethodAccess getMethodAccess() {
		return methodAccess;
	}
}
