
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
 * Requires that the integer attribute value meets the condition:
 * <code>attributeValue % {@link #modulo()}</code> == {@link #offset()}.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = IntModuloConstraint.class)
public @interface IntModulo
{
    /**
     * Modulo value.
     */
    int modulo();

    /**
     * Offset value.
     */
    int offset() default 0;
}
