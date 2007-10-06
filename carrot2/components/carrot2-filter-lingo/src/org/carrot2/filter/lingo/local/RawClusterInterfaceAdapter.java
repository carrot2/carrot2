
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

package org.carrot2.filter.lingo.local;

import java.util.ArrayList;
import java.util.List;

import org.carrot2.core.clustering.RawCluster;
import org.carrot2.core.clustering.RawClusterBase;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.filter.lingo.common.Cluster;
import org.carrot2.filter.lingo.common.Snippet;

/**
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public final class RawClusterInterfaceAdapter extends RawClusterBase {

	private List originalDocuments;
	private Cluster cluster;

	RawClusterInterfaceAdapter(Cluster cluster, List originalDocuments) {
		this.originalDocuments = originalDocuments;
		this.cluster = cluster;
        setScore(cluster.getScore());
		if (cluster.isJunk())
			super.setProperty( RawCluster.PROPERTY_JUNK_CLUSTER, Boolean.TRUE );
	}

	public List getClusterDescription() {
        return cluster.getLabelsAsList();
	}

	public List getSubclusters() {
        ArrayList subclusters = (ArrayList) cluster.getClustersAsList().clone();

        for (int i = subclusters.size()-1; i >= 0; i--) {
        	Cluster subcluster = (Cluster) subclusters.get(i);
        	RawClusterInterfaceAdapter adapter =
        		new RawClusterInterfaceAdapter( subcluster,
        				this.originalDocuments);
        	// replace with an adapter.
        	subclusters.set(i, adapter);
        }
        return subclusters;
	}

	public List getDocuments() {
        ArrayList documents = (ArrayList) cluster.getSnippetsAsArrayList().clone();

        for (int i = documents.size()-1; i >= 0 ; i--) {
            Object id = ((Snippet) documents.get(i)).getSnippetId();

            RawDocument rawDocument = (RawDocument) this.originalDocuments.get(
            		Integer.parseInt((String) id));
            rawDocument.setProperty(
                SnippetInterfaceAdapter.PROPERTY_CLUSTER_MEMBER_SCORE,
                new Double(cluster.getSnippetScore(i)));

            documents.set(i, rawDocument);
        }

        return documents;
	}
}
