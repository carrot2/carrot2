package org.carrot2.util.attribute.constraint;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface IsConstraint {
	Class<?> implementation();
}
