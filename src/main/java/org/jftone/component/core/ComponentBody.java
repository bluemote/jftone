package org.jftone.component.core;

public class ComponentBody<T> {
	private String name;
	
	private Class<T> clazz;
	
	private String scope;
	
	private String initMethod;
	
	private String destroyMethod;
	
	private T instance;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getInitMethod() {
		return initMethod;
	}

	public void setInitMethod(String initMethod) {
		this.initMethod = initMethod;
	}

	public String getDestroyMethod() {
		return destroyMethod;
	}

	public void setDestroyMethod(String destroyMethod) {
		this.destroyMethod = destroyMethod;
	}

	public T getInstance() {
		return instance;
	}

	public void setInstance(T instance) {
		this.instance = instance;
	}

	public Class<T> getClazz() {
		return clazz;
	}

	public void setClazz(Class<T> clazz) {
		this.clazz = clazz;
	}
	
}
