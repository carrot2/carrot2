package org.carrot2.util.attribute.constraint;

import java.io.File;
import java.lang.annotation.Annotation;

import org.simpleframework.xml.Root;

@Root(name = "is-directory")
class IsDirectoryConstraint extends IsFileConstraintBase
{
    @Override
    boolean isFileConstraintMet(File file)
    {
        return file.isDirectory();
    }

    @Override
    protected void populateCustom(Annotation annotation)
    {
        this.mustExist = ((IsDirectory) annotation).mustExist();
    }
}
