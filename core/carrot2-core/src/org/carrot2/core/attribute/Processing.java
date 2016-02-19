
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.attribute;

import java.lang.annotation.*;

import org.carrot2.core.IProcessingComponent;

/**
 * Marks attributes that will be bound before and after Carrot<sup>2</sup> component
 * performs processing. Please see {@link IProcessingComponent#beforeProcessing()} and
 * {@link IProcessingComponent#afterProcessing()} for details.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Processing
{
}
