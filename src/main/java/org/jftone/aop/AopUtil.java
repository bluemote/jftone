package org.jftone.aop;

import java.lang.reflect.Method;

import org.jftone.config.Const;
import org.jftone.dao.Dao;
import org.jftone.util.StringUtil;

public class AopUtil {
	/**
	 * 判断是否为toString"equals"方法.
	 * @see java.lang.Object#equals
	 */
	public static boolean isEqualsMethod(Method method) {
		return (method != null && method.getName().equals("equals") &&
				method.getParameterTypes().length == 1 && method.getParameterTypes()[0] == Object.class);
	}
	/**
	 * 判断是否为toString"hashCode"方法.
	 * @see java.lang.Object#hashCode
	 */
	public static boolean isHashCodeMethod(Method method) {
		return (method != null && method.getName().equals("hashCode") &&
				method.getParameterTypes().length == 0);
	}
	/**
	 * 判断是否为toString方法.
	 * @see java.lang.Object#toString()
	 */
	public static boolean isToStringMethod(Method method) {
		return (method != null && method.getName().equals("toString") &&
					method.getParameterTypes().length == 0);
	}
	
	public static boolean isSetDaoMethod(Method method) {
		return (method != null && method.getName().equals(Const.DAO_METHOE) &&
				method.getParameterTypes().length == 1 && method.getParameterTypes()[0] == Dao.class);
	}
	
	/**
	 * 解析切面通知对象方法
	 * @param methods
	 * @return
	 */
	public static AdviseIndicator parseAdviseIndicator(Method[] methods) {
		AdviseIndicator ai = new AdviseIndicator();
		for(Method m : methods) {
			if(AdvisorEnum.BEFORE.checkMethod(m)) {
				ai.setHasBefore(true);
				ai.setHasBeforeArg(AdvisorEnum.BEFORE.hasParamter(m));
			}else if(AdvisorEnum.AFTER.checkMethod(m)) {
				ai.setHasAfter(true);
				ai.setHasAfterArg(AdvisorEnum.AFTER.hasParamter(m));
			}else if(AdvisorEnum.AROUND.checkMethod(m)) {
				ai.setHasAround(true);		//环绕方法必须有入参
			}else if(AdvisorEnum.THROWING.checkMethod(m)) {
				ai.setHasThrowing(true);
				ai.setHasThrowingArg(AdvisorEnum.THROWING.hasParamter(m));
			}else if(AdvisorEnum.AFTERRETURNING.checkMethod(m)) {
				ai.setHasAfterReturning(true);
				ai.setHasAfterReturningArg(AdvisorEnum.AFTERRETURNING.hasParamter(m));
			}
		}
		return ai;
	}
	
	/**
	 * 解析类名表达式
	 * 
	 * @param expressionStr
	 * @return
	 */
	public static String parseClassName(String beanClassName, String... expressionArray) {
		StringBuilder strBuilder = new StringBuilder();
		int index = 0;
		for (String str : expressionArray) {
			if(StringUtil.isBlank(str)) {
				throw new IllegalArgumentException(beanClassName+"注解Aspect属性className错误，不能有空字符串");
			}
			if (index > 0) {
				strBuilder.append("|");
			}
			strBuilder.append(replaceClassNameSymbol(str));
			index++;
		}
		return strBuilder.toString();
	}

	/**
	 * 根据特定类名表达式替换为正则表达式
	 * 
	 * @param str
	 * @return
	 */
	private static String replaceClassNameSymbol(String str) {
		String expression = null;
		if (isEqualStarChar(str)) {
			expression = "^\\w*(\\.\\w+)*$";
		} else {
			expression = str.replace("*", "(\\w)*").replace("..", "(\\.\\w+)*\\.{1}");
		}
		return expression;
	}

	/**
	 * 解析类方法名表达式
	 * 
	 * @param expressionStr
	 * @return
	 */
	public static String parseMethodName(String beanClassName, String... expressionArray) {
		StringBuilder strBuilder = new StringBuilder();
		int index = 0;
		for (String str : expressionArray) {
			if(StringUtil.isBlank(str)) {
				throw new IllegalArgumentException(beanClassName+"注解Aspect属性className错误，不能有空字符串");
			}
			if (index > 0) {
				strBuilder.append("|");
			}
			strBuilder.append(replaceMethodNameSymbol(str));
			index++;
		}
		return strBuilder.toString();
	}

	/**
	 * 根据类方法名表达式替换为正则表达式
	 * 
	 * @param str
	 * @return
	 */
	private static String replaceMethodNameSymbol(String str) {
		String expression = null;
		if (isEqualStarChar(str)) {
			expression = "\\w*";
		} else {
			expression = str.replace("*", "(\\w)*");
		}
		return expression;
	}

	private static boolean isEqualStarChar(String str) {
		return str.equals("*") ? true : false;
	}

	public static boolean allowAll(String... strArray) {
		return strArray.length == 1 && strArray[0].equals("*") ? true : false;
	}
}
