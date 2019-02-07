
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute.constraint;

import java.lang.annotation.*;

/**
 * Requires that instances bound to the attribute are of one of the provided
 * {@link #classes()}.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = ImplementingClassesConstraint.class)
public @interface ImplementingClasses
{
    /**
     * The allowed classes for the attribute value.
     */
    Class<?> [] classes();

    /**
     * If <code>true</code>, only instances assignable to one of the listed
     * {@link #classes()} will be allowed. Otherwise, any instance assignable to the
     * attribute type will be allowed.
     */
    boolean strict() default true;
}
