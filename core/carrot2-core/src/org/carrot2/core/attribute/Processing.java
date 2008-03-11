package org.carrot2.core.attribute;

import java.lang.annotation.*;

import org.carrot2.core.ProcessingComponent;

/**
 * Marks attributes that will be bound before and after Carrot<sup>2</sup> component
 * performs processing. Please see {@link ProcessingComponent#beforeProcessing()} and
 * {@link ProcessingComponent#afterProcessing()} for details.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Processing
{
}
