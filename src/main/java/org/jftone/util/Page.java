/**
 * Page.java
 * 分页类
 * 
 * @author		zhoubing
 * @date   		Nov 2, 2011
 * @revision	v1.0
 */
package org.jftone.util;

public final class Page {
	private int pageSize;			//每页显示的条数
	private long recordCount;		//总共的条数
	private int currentPage;		//当前页面

	public Page() {
		this.pageSize = 20;			//默认20条记录
		this.currentPage = 1;		//默认第1页
	}

	public Page(int pageSize, int recordCount) {
		this(pageSize, recordCount, 1);
	}

	public Page(int pageSize, long recordCount, int currentPage) {
		this.pageSize = pageSize;
		this.recordCount = recordCount;
		this.setCurrentPage(currentPage);
	}
	
	/**
	 * 返回页面显示记录数
	 * @return
	 */
	public int getPageSize() {
		return pageSize;
	}
	
	/**
	 * 设置页面记录数
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 返回总记录数
	 * @return
	 */
	public long getRecordCount() {
		return recordCount;
	}

	/**
	 * 设置总记录数
	 * @param recordCount
	 */
	public void setRecordCount(long recordCount) {
		this.recordCount = recordCount;
	}
	
	/**
	 * 返回当前页
	 */ 
	public int getCurrentPage() {
		if(currentPage > getPageCount()){
			return getPageCount();
		}
		return currentPage;
	}

	/**
	 * 设置当前页
	 * @param currentPage
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage <= 0 ? 1 : currentPage;
	}
	
	/**
	 * 获取总页面数
	 * @return
	 */
	public int getPageCount() {
		int size = (int)recordCount/pageSize;			//总条数/每页显示的条数=总页数
		int mod = (int)recordCount%pageSize;			// 最后一页的条数
		if (mod != 0)
			size++;
		return recordCount == 0? 1 : size;
	}

	/**
	 * 返回数据库起始索引
	 * 数据库索引以0开始
	 * @return
	 */
	public long getStart() {
		return (getCurrentPage()-1)*pageSize;
	}

}
