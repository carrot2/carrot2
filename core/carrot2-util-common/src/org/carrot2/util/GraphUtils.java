
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

package org.carrot2.util;

import java.util.List;

import bak.pcj.list.*;
import bak.pcj.set.IntSet;

import com.google.common.collect.Lists;

/**
 * Various utilities for processing graphs
 */
public class GraphUtils
{
    /**
     * Finds coherent subgraphs of an undirected graph.
     * 
     * @param vertexCount the number of vertices in the graph
     * @param arcPredicate a predicate that determines which vertices are connected by an
     *            arc
     * @param pruneOneNodeSubrgaphs if <code>true</code>, one-node subgraphs will not be
     *            included in the result
     * @return a list of {@link IntSet}s containing vertices of the coherent subgraphs
     */
    public static List<IntList> findCoherentSubgraphs(int vertexCount,
        ArcPredicate arcPredicate, boolean pruneOneNodeSubrgaphs)
    {
        // Find coherent sub-graphs using breadth-first search
        final boolean [] nodesChecked = new boolean [vertexCount];
        final List<IntList> clusterGroups = Lists.newArrayList();
        for (int i = 0; i < vertexCount; i++)
        {
            if (!nodesChecked[i])
            {
                IntDeque nodeQueue = new IntArrayDeque(vertexCount);
                nodeQueue.add(i);
                nodesChecked[i] = true;
                IntArrayList clusterGroup = new IntArrayList();

                while (!nodeQueue.isEmpty())
                {
                    // Get a node from the queue
                    int node = nodeQueue.get(0);
                    nodeQueue.removeElementAt(0);

                    // Add to the current sub-graph (cluster group)
                    clusterGroup.add(node);

                    // Add all its non-checked neighbors to the queue
                    for (int j = i + 1; j < vertexCount; j++)
                    {
                        if (!nodesChecked[j])
                        {
                            if (arcPredicate.isArcPresent(node, j))
                            {
                                nodeQueue.add(j);
                                nodesChecked[j] = true;
                            }
                        }
                    }
                }

                if (clusterGroup.size() > 1 || !pruneOneNodeSubrgaphs)
                {
                    clusterGroups.add(clusterGroup);
                }
            }
        }

        return clusterGroups;
    }

    /**
     * A predicate defining arcs of an undirected graph.
     */
    public static interface ArcPredicate
    {
        /**
         * Returns <code>true</code> if there is an arc connecting <code>vertexA</code>
         * and <code>vertexB</code>.
         */
        public boolean isArcPresent(int vertexA, int vertexB);
    }
}
