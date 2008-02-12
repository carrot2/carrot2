package org.carrot2.core.constraint;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = ImplementingClassesConstraint.class)
public @interface ImplementingClasses {
    Class<?> [] classes();
}
