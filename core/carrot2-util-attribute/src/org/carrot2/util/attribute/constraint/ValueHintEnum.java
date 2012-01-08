
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute.constraint;

import java.lang.annotation.*;

import org.carrot2.util.attribute.Required;

/**
 * A set of values for an attribute of type {@link String}. This can be either a hint
 * (for user interfaces) or a restriction (which causes the attribute to behave much like
 * an enum type).
 * <p>
 * By default the constraint accepts values returned from {@link Enum#name()}. If the
 * enum type returned from {@link #values()} implements {@link IValueHintMapping}
 * interface, values checked are retrieved from the
 * {@link IValueHintMapping#getAttributeValue()} method of each individual constant.
 * 
 * @see Required
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = ValueHintEnumConstraint.class)
public @interface ValueHintEnum
{
    Class<? extends Enum<?>> values();
}
