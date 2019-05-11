package org.jftone.datasource;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询算法
 * @author zhoubing
 *
 */
final class MutipleDataSourcePoll implements IDataSourceElect{
	
	private int size = 0;
	private AtomicInteger counter = new AtomicInteger(-1);
	
	MutipleDataSourcePoll(int len){
		size = len; 
	}
	
	public int getIndex() {
		int index = counter.incrementAndGet() % size;
        if (counter.get() > Integer.MAX_VALUE) {
        	//超过最大数后还原，重新开始
        	counter.set(-1); 
        }
        return index;
	}
	 
}
