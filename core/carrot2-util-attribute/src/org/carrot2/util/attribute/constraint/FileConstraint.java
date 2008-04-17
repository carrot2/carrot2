package org.carrot2.util.attribute.constraint;

import java.io.File;

public class FileConstraint extends Constraint
{

    @Override
    boolean isMet(Object value)
    {
        if (value instanceof File)
        {
            File file = (File) value;
            if (annotation instanceof IsDirectory)
            {
                return (file.exists() && file.isDirectory());
            }
            else
            {
                return (file.exists() && file.isFile());
            }
        }
        else
        {
            return false;
        }
    }

}
