package org.carrot2.core.attribute;

import java.lang.annotation.*;

/**
 * Marks attributes that will be bound upon initialization of a Carrot<sup>2</sup>
 * component. Please see {@link ProcessingComponent#init(ControllerContext))} for details.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Init
{
}
