
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
 * Requires that instances bound to the attribute are a valid result of the 
 * attribute's class static <tt>valueOf</tt> method.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = PassesValueOfConstraint.class)
public @interface PassesValueOf
{
    Class<?> valueOfClass();
}
