package org.jftone.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public final class MathUtil {

	/**
	 * 按照指定的格式将数字类对象转换为指定格式的字符串类型
	 * @param obj
	 * @param format
	 * @return
	 */
	public static String fmtNumberToString(Object obj, String format) {
		DecimalFormat df = new DecimalFormat(format);
		return df.format(obj);
	}


	/**
	 * 默认转换类型
	 * @param obj
	 * @return
	 */
	public static String format(Object obj) {
		return fmtNumberToString(obj, "###.00");
	}
	
	/**
	 * 四舍五入保留两位小数
	 * @param d
	 * @return
	 */
	public static float round(double d){
		return Math.round(d*100)/100.00f;
	}
	
	/**
	 * 不规则矩阵转换	
	 * @param <T>
	 * @param matrix
	 */
    public static List<Object[]> convertMatrix(List<Object[]> matrixs) {  
    	int length = matrixs.size();
    	List<Object[]> resultList = new ArrayList<Object[]>(); 
    	assocMatrixAttr(matrixs, 0, length, new Object[length], resultList);
    	return resultList;
    } 
    
    private static void assocMatrixAttr(List<Object[]> matrixs, int idx, int size, Object[] retObj, List<Object[]> retList)
	{
		if(idx>=size) {
			retList.add(retObj);
			return;
		}
		Object[] matrix = matrixs.get(idx);
    	int curLen = matrix.length;
		for(int i=0; i<curLen; i++) {
			Object[] tmpObj = retObj.clone();
			tmpObj[idx] = matrix[i];
			assocMatrixAttr(matrixs, idx+1, size, tmpObj, retList);
		}
	}
}
