/**
 * DataSort.java
 * 
 * zhoubing
 * Jul 4, 2019
 */
package org.jftone.dao;

public class DataSort {
	public static final String DESC = "DESC";	//降序
	public static final String ASC = "ASC";		//升序

	private String property;
	private String sort;
	
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}

	
}
