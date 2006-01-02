
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.chilang.carrot.filter.cluster.rough.clustering;

import com.chilang.carrot.filter.cluster.rough.data.IRContext;


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
     * @return
     */
    public XCluster[] getClusters();

    void setContext(IRContext context);
}
