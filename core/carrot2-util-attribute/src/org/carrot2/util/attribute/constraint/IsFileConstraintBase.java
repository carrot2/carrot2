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

        checkAssignableFrom(File.class, value);

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
