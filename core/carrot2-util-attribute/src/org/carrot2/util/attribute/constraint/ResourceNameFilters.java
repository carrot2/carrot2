package org.carrot2.util.attribute.constraint;

import java.lang.annotation.*;

/**
 * A resource name filter (not a constraint, just a hint for editors). 
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceNameFilters
{
    ResourceNameFilter [] filters();
}
