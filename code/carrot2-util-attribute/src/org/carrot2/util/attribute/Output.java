package org.carrot2.util.attribute;

import java.lang.annotation.*;

/**
 * Denotes fields whose values can be read (collected) by {@link AttributeBinder}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Output
{
}
