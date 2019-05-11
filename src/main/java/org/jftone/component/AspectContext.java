package org.jftone.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jftone.aop.CustomizedPointcut;
import org.jftone.aop.InterceptorFactory;
import org.jftone.aop.TransactionalPointcut;

public final class AspectContext {

	private static TransactionalPointcut transactionalPointcut = null;
	
	private static List<CustomizedPointcut> aspectList = new ArrayList<CustomizedPointcut>();

	public static void add(CustomizedPointcut aspectBox) {
		aspectList.add(aspectBox);
	}

	/**
	 * 自定义切面按照顺序排列
	 */
	public static void sort() {
		Collections.sort(aspectList, new Comparator<CustomizedPointcut>() {
			@Override
			public int compare(CustomizedPointcut o1, CustomizedPointcut o2) {
				return o2.getOrder() - o1.getOrder();
			}

		});
	}

	public static int getCount() {
		return aspectList.size();
	}

	public static void clear() {
		aspectList.clear();
		aspectList = null;
	}

	public static List<CustomizedPointcut> getAspectPointcuts() {
		return aspectList;
	}
	
	public static InterceptorFactory getInterceptorFactory() {
		return InterceptorFactory.getInstance();
	}

	public static TransactionalPointcut getTransactionalPointcut() {
		return transactionalPointcut;
	}

	public static void setTransactionalPointcut(TransactionalPointcut transactionalPointcut) {
		AspectContext.transactionalPointcut = transactionalPointcut;
	}
	
	
}
