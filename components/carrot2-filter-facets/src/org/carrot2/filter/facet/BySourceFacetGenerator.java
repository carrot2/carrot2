
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

package org.carrot2.filter.facet;

import org.carrot2.core.clustering.RawDocument;

/**
 * Divides the input documents according to the search source they came from. The search
 * source is determined based on the {@link RawDocument#PROPERTY_SOURCES} property. This
 * class is thread-safe.
 *
 * @author Stanislaw Osinski
 */
public class BySourceFacetGenerator extends ByArrayPropertyFacetGenerator
{
    public static BySourceFacetGenerator INSTANCE = new BySourceFacetGenerator();

    public BySourceFacetGenerator()
    {
        super(RawDocument.PROPERTY_SOURCES);
    }
}
