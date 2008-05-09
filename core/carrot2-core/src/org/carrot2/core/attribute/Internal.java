package org.carrot2.core.attribute;

import java.lang.annotation.*;

/**
 * Denotes attributes the end-user applications should not attempt to rended in their User
 * Interfaces.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Internal
{
}
