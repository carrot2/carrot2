package org.carrot2.util.attribute.constraint;

import java.io.File;
import java.lang.annotation.Annotation;

import org.simpleframework.xml.Root;

@Root(name = "is-file")
class IsFileConstraint extends IsFileConstraintBase
{
    @Override
    boolean isFileConstraintMet(File file)
    {
        return file.isFile();
    }

    @Override
    protected void populateCustom(Annotation annotation)
    {
        this.mustExist = ((IsFile) annotation).mustExist();
    }
}
