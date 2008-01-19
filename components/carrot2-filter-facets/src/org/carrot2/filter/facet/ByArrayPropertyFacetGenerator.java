
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

import java.util.*;

import org.apache.commons.collections.MapUtils;
import org.carrot2.core.clustering.*;

/**
 * Divides the input documents according to the value of an array-valued property of the
 * documents, e.g. {@link RawDocument#PROPERTY_SOURCES} or
 * {@link RawDocument#PROPERTY_KEYWORDS}. This class is thread-safe.
 * 
 * @author Stanislaw Osinski
 */
public class ByArrayPropertyFacetGenerator implements FacetGenerator
{
    private final String property;

    public ByArrayPropertyFacetGenerator(String property)
    {
        this.property = property;
    }

    public List generateFacets(List rawDocuments)
    {
        Map sourcesToDocument = MapUtils.multiValueMap(new HashMap());
        List unknownSource = new ArrayList();

        // Group by source
        for (Iterator it = rawDocuments.iterator(); it.hasNext();)
        {
            RawDocument rawDocument = (RawDocument) it.next();
            String [] sources = (String []) rawDocument.getProperty(property);

            boolean sourceFound = false;
            if (sources != null)
            {
                for (int i = 0; i < sources.length; i++)
                {
                    if (sources[i] != null && sources[i].length() > 0)
                    {
                        sourcesToDocument.put(sources[i], rawDocument);
                        sourceFound = true;
                    }
                }
            }

            if (!sourceFound)
            {
                unknownSource.add(rawDocument);
            }
        }

        // Convert to RawClusters
        List rawClusters = new ArrayList(sourcesToDocument.size() + 1);
        for (Iterator it = sourcesToDocument.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry) it.next();
            final String label = (String) entry.getKey();
            final List documents = (List) entry.getValue();

            RawClusterBase rawCluster = new RawClusterBase();
            rawCluster.addLabel(label);
            rawCluster.addDocuments(documents);

            rawClusters.add(rawCluster);
        }

        // Sort clusters
        Collections.sort(rawClusters, RawClusterUtils.BY_SIZE_AND_NAME_COMPARATOR);

        // Add "Unknown" source group
        if (unknownSource.size() > 0)
        {
            RawClusterBase rawCluster = new RawClusterBase();
            rawCluster.addLabel("Unknown");
            rawCluster.addDocuments(unknownSource);

            rawClusters.add(rawCluster);
        }

        return rawClusters;
    }
}
