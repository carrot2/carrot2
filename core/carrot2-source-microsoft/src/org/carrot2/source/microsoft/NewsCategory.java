
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
 * News category for {@link Bing3NewsDocumentSource}.
 */
public enum NewsCategory
{
    BUSINESS("rt_Business"),
    ENTERTAINMENT("rt_Entertainment"), 
    HEALTH("rt_Health"),
    POLITICS("rt_Politics"), 
    SPORTS("rt_Sports"),
    US("rt_US"),
    WORLD("rt_World"), 
    SCIENCE("rt_ScienceAndTechnology");

    String catValue;

    private NewsCategory(String catValue)
    {
        this.catValue = catValue;
    }
    
    @Override
    public String toString()
    {
        return StringUtils.identifierToHumanReadable(name());
    }
}
