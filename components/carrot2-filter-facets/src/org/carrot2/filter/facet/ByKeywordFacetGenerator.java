/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
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
 * Divides the input documents according to the keywords the search engine attached to
 * them. The keywords are determined based on the {@link RawDocument#PROPERTY_KEYWORDS}
 * property. This class is thread-safe.
 * 
 * @author Stanislaw Osinski
 */
public class ByKeywordFacetGenerator extends ByArrayPropertyFacetGenerator
{
    public static ByKeywordFacetGenerator INSTANCE = new ByKeywordFacetGenerator();

    public ByKeywordFacetGenerator()
    {
        super(RawDocument.PROPERTY_KEYWORDS);
    }
}
