package org.jftone.datasource;

import java.util.Random;

/**
 * 权重比计算
 * @author zhoubing
 *
 */
final class MutipleDataSourceWeight implements IDataSourceElect{
	
	private int size;
	private int[] weights;
	private int sum;
	
	MutipleDataSourceWeight(int len, int[] weights){
		this.size = len;
		this.weights = weights;
		for(int i=0; i<size; i++){
			sum += weights[i];
		}
	}
	
	public int getIndex() {
		Random random = new Random(sum);
		int randVal = random.nextInt();
		int index = 0;
		for(int i = 0; i<size; i++) {  
			if (randVal <= weights[i]){  
				index = i;  
	            break;  
	        }  
	        randVal -= weights[i];  
	    } 
        return index;
	}
}
