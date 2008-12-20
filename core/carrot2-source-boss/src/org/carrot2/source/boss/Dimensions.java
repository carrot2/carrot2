
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.boss;


/**
 * Preferred image size for {@link BossImageSearchService#dimensions}.
 */
public enum Dimensions
{
    ALL("all"), 
    SMALL("small"), 
    MEDIUM("medium"), 
    LARGE("large"), 
    WALLPAPER("wallpaper"), 
    WIDE_WALLPAPER("widewallpaper");

    String parameterValue;

    Dimensions(String value)
    {
        this.parameterValue = value;
    }
}
