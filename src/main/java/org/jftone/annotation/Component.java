package org.jftone.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jftone.config.Const;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Component {
	String name() default "";
	String scope() default Const.SCOPE_SINGLETON;
	String init() default "";
	String destroy() default "";
}
