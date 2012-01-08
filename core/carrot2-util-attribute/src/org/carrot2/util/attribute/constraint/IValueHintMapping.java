
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


/**
 * This interface provides secondary mapping between enum constants, user-interface
 * (user-friendly) names and attribute values. All these elements have their own
 * constraints (enum constants must be valid Java identifiers, for example) and just
 * overriding {@link Enum#toString()} method is not enough to provide this functionality.
 * 
 * @see ValueHintMappingUtils
 */
public interface IValueHintMapping
{
    public String getUserFriendlyName();
    public String getAttributeValue();
}
