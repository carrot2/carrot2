
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
 * Requires that the File object is a directory.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = IsDirectoryConstraint.class)
public @interface IsDirectory
{
    /**
     * If set to <code>true</code>, the provided object must be a {@link File} instance,
     * and the directory pointed to by it must exist. If set to <code>false</code>, only the
     * type of the provided object is checked.
     */
    boolean mustExist() default true;
}
