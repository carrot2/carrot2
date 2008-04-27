package org.carrot2.webapp.attribute;

import java.lang.annotation.*;

/**
 * Marks attributes that will be bound based on the HTTP request parameters.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Request
{
}
