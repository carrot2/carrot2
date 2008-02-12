package org.carrot2.core.constraint;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation=IntModuloConstraint.class)
public @interface IntModulo {
	int modulo();
	int offset() default 0;
}
