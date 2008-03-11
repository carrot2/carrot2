package org.carrot2.util.attribute;

import java.lang.annotation.*;

/**
 * Denotes fields whose values can be set (written) by {@link AttributeBinder}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Input
{
}
