
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

package org.carrot2.filter.trc.carrot.filter.cluster.rough.clustering;


/**
 * Label generator for a set of clusters
 */
public interface ClusterLabelGenerator {


    /**
     * Generate label for a cluster identified by an id
     * @param id
     */
    public String[] getLabel(int id);
}
