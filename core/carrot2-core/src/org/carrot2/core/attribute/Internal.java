
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.attribute;

import java.lang.annotation.*;

/**
 * Denotes attributes the end-user applications may not want to display in their User
 * Interfaces.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Internal
{
    /**
     * Indicates that this internal attribute defines the component's configuration. Some
     * end user applications may still choose to display editors for internal
     * configuration attributes, others will hide them for security reasons.
     */
    boolean configuration() default false;
}
