
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

import org.simpleframework.xml.Attribute;

abstract class IsFileConstraintBase extends Constraint
{
    @Attribute(name = "must-exist")
    boolean mustExist;

    @Override
    protected boolean isMet(Object value)
    {
        if (value == null)
        {
            return false;
        }

        checkAssignableFrom(value, File.class);

        final File file = (File) value;
        if (mustExist || file.exists())
        {
            return isFileConstraintMet(file);
        }
        else
        {
            // If it doesn't exist, it can surely be made a file or directory
            return true;
        }
    }

    abstract boolean isFileConstraintMet(File file);
}
