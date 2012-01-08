
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

import java.io.File;
import java.lang.annotation.*;


/**
 * Requires that the provided object is an instance of {@link File} and the path denoted
 * by it is actually a file.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = IsFileConstraint.class)
public @interface IsFile
{
    /**
     * If set to <code>true</code>, the provided object must be a {@link File} instance,
     * and the file pointed to by it must exist. If set to <code>false</code>, only the
     * type of the provided object is checked.
     */
    boolean mustExist() default true;
}
