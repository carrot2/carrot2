
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

package org.carrot2.source.microsoft;

import org.carrot2.util.StringUtils;

/**
 * Safe search modes for Bing API.
 */
public enum AdultOption
{
    OFF, MODERATE, STRICT;

    @Override
    public String toString()
    {
        return StringUtils.identifierToHumanReadable(name());
    }
}
