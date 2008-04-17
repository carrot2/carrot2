package org.carrot2.util.attribute.constraint;

import java.lang.annotation.*;

/**
 * Requires that the File object is a directory.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = FileConstraint.class)
public @interface IsDirectory
{

}
