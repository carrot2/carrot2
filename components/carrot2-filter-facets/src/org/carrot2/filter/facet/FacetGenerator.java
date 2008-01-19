
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

import java.util.List;

import org.carrot2.core.clustering.RawCluster;

/**
 * Divides the input documents into groups corresponding to different synthetic properties
 * of the documents, e.g. the search source they come from or their domain names.
 *
 * TODO: This interface is pretty much generic, so for the 3.0 release
 * it could be refactored/replaced by a general "cluster generator" interface.
 *
 * @author Stanislaw Osinski
 */
public interface FacetGenerator
{
    /**
     * Returns a list of {@link RawCluster}s corresponding to the facets
     * discovered in input documents.
     *
     * @param rawDocuments
     * @return the list of facets discovered in the input documents 
     */
    public List generateFacets(List rawDocuments);
}
