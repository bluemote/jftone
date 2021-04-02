package org.jftone.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Transactional {
	String[] value() default {};
	boolean readOnly() default false;
	Isolation isolation() default Isolation.DEFAULT;
}
