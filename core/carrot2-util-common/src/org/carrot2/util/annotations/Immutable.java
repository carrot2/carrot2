
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
 * Marker interface for classes that are immutable (once created, never change their state.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
public @interface Immutable
{
    /**
     * Indicates if objects of this class can be safely published (used by other threads
     * without explicit synchronization). Safe publication usually involves initialization of
     * <code>final</code> fields in the constructor. 
     */
    boolean safePublication() default false;
}
