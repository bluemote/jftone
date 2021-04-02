package org.jftone.aop;

import java.lang.reflect.Method;

public enum AdvisorEnum {
	BEFORE("before") {
		@Override
		public boolean hasParamter(Method method) {
			Class<?>[] types = method.getParameterTypes();
			if (types.length == 0) {
				return false;
			} else if (types.length == 1 && types[0] == JoinPoint.class) {
				return true;
			}
			return false;
		}
	},
	AFTER("after") {
		@Override
		public boolean hasParamter(Method method) {
			Class<?>[] types = method.getParameterTypes();
			if (types.length == 0) {
				return false;
			} else if (types.length == 1 && types[0] == JoinPoint.class) {
				return true;
			}
			return false;
		}
	},
	AROUND("around") {
		@Override
		public boolean hasParamter(Method method) {
			return true;
		}

	},
	THROWING("throwing") {
		@Override
		public boolean hasParamter(Method method) {
			Class<?>[] types = method.getParameterTypes();
			if (types.length == 0) {
				return false;
			} else if (types.length == 2 && types[0] == JoinPoint.class && types[1] == Throwable.class) {
				return true;
			}
			return false;
		}
	},
	AFTERRETURNING("afterReturning") {
		@Override
		public boolean hasParamter(Method method) {
			Class<?>[] types = method.getParameterTypes();
			if (types.length == 0) {
				return false;
			} else if (types.length == 2 && types[0] == JoinPoint.class && types[1] == Object.class) {
				return true;
			}
			return false;
		}
	};

	private String methodName;

	AdvisorEnum(String methodName) {
		this.methodName = methodName;
	}

	String methodName() {
		return this.methodName;
	}

	/**
	 * 判断方法名是否相同
	 * 
	 * @param method
	 * @return
	 */
	boolean checkMethod(Method method) {
		return this.methodName.equals(method.getName()) && method.getReturnType() == Void.TYPE;
	}

	public abstract boolean hasParamter(Method method);
}
