
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.microsoft;

import org.carrot2.util.StringUtils;

import com.microsoft.msnsearch.SafeSearchOptions;

/**
 * Safe search modes for Live API.
 */
public enum SafeSearch
{
    OFF, MODERATE, STRICT;

    /**
     * Converts enum constant to a {@link SafeSearchOptions}.
     */
    SafeSearchOptions getSafeSearchOption()
    {
        switch (this)
        {
            case OFF:
                return SafeSearchOptions.Off;
            case MODERATE:
                return SafeSearchOptions.Moderate;
            case STRICT:
                return SafeSearchOptions.Strict;
            default:
                throw new RuntimeException("Unhandled constant: " + this);
        }
    }

    @Override
    public String toString()
    {
        return StringUtils.identifierToHumanReadable(name());
    }
}
