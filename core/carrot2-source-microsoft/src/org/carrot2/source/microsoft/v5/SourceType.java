
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

package org.carrot2.source.microsoft.v5;

import java.util.Locale;

import org.carrot2.util.StringUtils;

/**
 * Source type for Bing5 searches.
 */
public enum SourceType
{
    WEBPAGES, 
    NEWS, 
    IMAGES;

    @Override
    public String toString()
    {
        return StringUtils.identifierToHumanReadable(name());
    }

    final String responseFilterName = this.name().toLowerCase(Locale.ROOT);

    String responseFilter() {
      return responseFilterName;
    }
}
