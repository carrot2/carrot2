package org.carrot2.util.attribute;

import java.lang.annotation.*;

/**
 * A test attribute filtering annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TestInit
{
}
