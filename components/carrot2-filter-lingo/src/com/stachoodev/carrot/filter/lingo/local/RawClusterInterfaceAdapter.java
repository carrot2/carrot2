/*
 * Carrot2 Project Copyright (C) 2002-2005, Dawid Weiss Portions (C)
 * Contributors listed in carrot2.CONTRIBUTORS file. All rights reserved.
 * 
 * Refer to the full license file "carrot2.LICENSE" in the root folder of the
 * CVS checkout or at: http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.filter.lingo.local;

import java.util.ArrayList;
import java.util.List;

import com.dawidweiss.carrot.core.local.clustering.RawCluster;
import com.dawidweiss.carrot.core.local.clustering.RawClusterBase;
import com.dawidweiss.carrot.core.local.clustering.RawDocument;
import com.stachoodev.carrot.filter.lingo.common.Cluster;
import com.stachoodev.carrot.filter.lingo.common.Snippet;

/**
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public final class RawClusterInterfaceAdapter extends RawClusterBase {

	private List originalDocuments;
	private Cluster cluster;
	
	/**
	 * @param cluster
	 * @param documents
	 */
	RawClusterInterfaceAdapter(Cluster cluster, List originalDocuments) {
		this.originalDocuments = originalDocuments;
		this.cluster = cluster;
		if (cluster.isJunk())
			super.setProperty( RawCluster.PROPERTY_JUNK_CLUSTER, Boolean.TRUE );
	}

	/*
	 * @see com.dawidweiss.carrot.core.local.clustering.RawCluster#getClusterDescription()
	 */
	public List getClusterDescription() {
        return cluster.getLabelsAsList();
	}

	/*
	 * @see com.dawidweiss.carrot.core.local.clustering.RawCluster#getSubclusters()
	 */
	public List getSubclusters() {
        ArrayList subclusters = (ArrayList) cluster.getClustersAsList().clone();

        int max = subclusters.size();
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

	/*
	 * @see com.dawidweiss.carrot.core.local.clustering.RawCluster#getDocuments()
	 */
	public List getDocuments() {
        ArrayList documents = (ArrayList) cluster.getSnippetsAsArray().clone();

        for (int i = documents.size()-1; i >= 0 ; i--) {
            Object id = ((Snippet) documents.get(i)).getSnippetId();

            RawDocument rawDocument = (RawDocument) this.originalDocuments.get(
            		Integer.parseInt((String) id));

            documents.set(i, rawDocument);
        }

        return documents;
	}

}
