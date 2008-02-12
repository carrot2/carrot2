package org.carrot2.core.constraint;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface IsConstraint {
	Class<?> implementation();
}
