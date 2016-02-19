
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

package org.carrot2.util.annotations;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;

/**
 * Marker interface for applying to code elements that are modified using
 * aspects to facilitate tracking.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, CONSTRUCTOR, METHOD, TYPE})
public @interface AspectModified
{
    String value() default "";
}
