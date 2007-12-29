package org.carrot2.core.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation=IntModuloConstraint.class)
public @interface IntModulo {
	int modulo();
	int offset() default 0;
}
