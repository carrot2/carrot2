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

package org.carrot2.core.clustering;

import java.util.*;

/**
 * Various utility method for operating on {@link RawCluster}s.
 *
 * @author Stanislaw Osinski
 */
public final class RawClusterUtils
{
    // TODO: maybe we should make it a public property, so that similar
    // calculations are not needed further down the chain?
    private final static String PROPERTY_CLUSTER_SIZE_CACHE = "csc";

    /**
     * No instantiation.
     */
    private RawClusterUtils()
    {
    }

    /**
     * Compares clusters first by their size (number of unique documents, including
     * subclusters, larger cluster is <b>smaller</b>, so that it is towards the beginning
     * of the list), in case of equal sizes, natural order of the first labels decides.
     */
    public static final Comparator BY_SIZE_AND_NAME_COMPARATOR = new Comparator()
    {
        public int compare(Object objectA, Object objectB)
        {
            final RawCluster clusterA = (RawCluster) objectA;
            final RawCluster clusterB = (RawCluster) objectB;

            if (objectA == objectB)
            {
                return 0;
            }

            if (objectA == null)
            {
                return -1;
            }

            if (objectB == null)
            {
                return 1;
            }

            final int sizeA = calculateSize(clusterA);
            final int sizeB = calculateSize(clusterB);

            if (sizeA != sizeB)
            {
                return sizeB - sizeA;
            }
            else
            {
                List labelA = clusterA.getClusterDescription();
                List labelB = clusterB.getClusterDescription();
                String stringLabelA = (labelA.size() > 0 ? (String) labelA.get(0) : null);
                String stringLabelB = (labelB.size() > 0 ? (String) labelB.get(0) : null);

                if (stringLabelA == null && stringLabelB == null)
                {
                    return 0;
                }
                else
                {
                    if (stringLabelA != null)
                    {
                        return stringLabelA.compareTo(stringLabelB);
                    }
                    else
                    {
                        return stringLabelB.compareTo(stringLabelA);
                    }
                }
            }
        }
    };

    /**
     * Calculates the size of a {@link RawCluster}, which is the number of unique
     * documents it contains, including its subclusters. Note: this method assumes that
     * the class implementing the {@link RawDocument} interface properly implements the
     * {@link Object#equals(Object)} and {@link Object#hashCode()} methods.
     */
    public static int calculateSize(RawCluster cluster)
    {
        Integer sizeCache = (Integer) cluster.getProperty(PROPERTY_CLUSTER_SIZE_CACHE);
        if (sizeCache == null)
        {
            sizeCache = new Integer(calculateSize(cluster, new HashSet()));
            cluster.setProperty(PROPERTY_CLUSTER_SIZE_CACHE, sizeCache);
        }

        return sizeCache.intValue();
    }

    private static int calculateSize(RawCluster cluster, Set docs)
    {
        if (cluster == null)
        {
            return docs.size();
        }

        docs.addAll(cluster.getDocuments());
        List subclusters = cluster.getSubclusters();
        for (Iterator it = subclusters.iterator(); it.hasNext();)
        {
            RawCluster subcluster = (RawCluster) it.next();
            calculateSize(subcluster, docs);
        }

        return docs.size();
    }
}
