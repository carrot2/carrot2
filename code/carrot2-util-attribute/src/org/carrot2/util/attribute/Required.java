package org.carrot2.util.attribute;

import java.lang.annotation.*;

/**
 * Marks required attributes. {@link AttributeBinder} will throw an exception when there
 * is no value provided for this attribute, or if the value provided is <code>null</code>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Required
{
}
