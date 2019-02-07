
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

/**
 * The level of complexity involved in tuning an attribute value. 
 */
public enum AttributeLevel
{
    /**
     * Attribute value easy to tune, easy to understand for a casual user.
     */
    BASIC("Basic"),

    /**
     * Attribute value easy to tune for a user familiar with text processing.
     */
    MEDIUM("Medium"),

    /**
     * Deep knowledge of the algorithm internals required for successful tuning.
     */
    ADVANCED("Advanced");

    /**
     * Capitalized version of the enum.
     */
    private final String capitalized;

    private AttributeLevel(String capitalized)
    {
        this.capitalized = capitalized;
    }
    
    @Override
    public String toString()
    {
        return capitalized;
    }
}
