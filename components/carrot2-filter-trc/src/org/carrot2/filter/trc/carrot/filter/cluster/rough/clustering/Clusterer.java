
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

package org.carrot2.filter.trc.carrot.filter.cluster.rough.clustering;

import org.carrot2.filter.trc.carrot.filter.cluster.rough.data.IRContext;


/**
 * Clusterer can cluster collection of objects
 * and clustering results can be collected.
 * Provide simple interface that wrap clustering algorithm and its context data
 */
public interface Clusterer {



    /**
     * Run clustering
     */
    public void cluster();


    /**
     * Return resulting clusters
     */
    public XCluster[] getClusters();

    void setContext(IRContext context);
}
