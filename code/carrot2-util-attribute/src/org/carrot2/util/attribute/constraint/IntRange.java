package org.carrot2.util.attribute.constraint;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation=RangeConstraint.class)
public @interface IntRange {
	int min();
	int max();
}
