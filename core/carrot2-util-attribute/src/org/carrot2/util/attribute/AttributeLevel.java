
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

package org.carrot2.util.attribute;

import org.apache.commons.lang.StringUtils;

/**
 * The level of complexity involved in tuning an attribute value. 
 */
public enum AttributeLevel
{
    /**
     * Attribute value easy to tune, easy to understand for a casual user.
     */
    BASIC,

    /**
     * Attribute value easy to tune for a user familiar with intelligent text processing.
     */
    MEDIUM,

    /**
     * Deep knowledge of the algorithm internals required for successful tuning.
     */
    ADVANCED;

    @Override
    public String toString()
    {
        return StringUtils.capitalize(name().toLowerCase());
    }
}
