
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.kgolembniak.carrot.filter.haog.algorithm;

import java.util.Iterator;
import java.util.List;

import com.dawidweiss.carrot.core.local.ProcessingException;
import com.dawidweiss.carrot.core.local.clustering.RawClusterBase;
import com.dawidweiss.carrot.core.local.clustering.RawClustersConsumer;
import com.dawidweiss.carrot.core.local.clustering.RawDocument;
import com.dawidweiss.carrot.core.local.clustering.TokenizedDocument;
import com.dawidweiss.carrot.filter.stc.StcParameters;
import com.dawidweiss.carrot.filter.stc.suffixtree.ExtendedBitSet;

/**
 * This class renders {@link com.dawidweiss.carrot.core.local.clustering.RawCluster}
 * from vertices retrieved from STC based algorithm.
 * @see com.kgolembniak.carrot.filter.haog.algorithm.GraphRenderer
 * @author Karol Gołembniak
 */
public class STCGraphRenderer implements GraphRenderer {
	
	/**
	 * Documents retrieved from user's query
	 */
	private List documents;
	/**
	 * Parameters for clusters creation.
	 */
	private StcParameters params;
    
	/**
     * This method creates output raw claster from internal results representation.
     * @throws ProcessingException 
     */
    public void renderRawClusters(List kernel, List documents, Object params,
   	RawClustersConsumer consumer) throws ProcessingException {
    	this.documents = documents;
    	this.params = (StcParameters) params;
    	
    	CombinedVertex kernelVertex;
		RawClusterBase rawCluster;
		//Reverse makes more complex cluster to be first on the list.
    	for (int i1=kernel.size()-1; i1>=0; i1--){
    		kernelVertex = (CombinedVertex) kernel.get(i1);
    		rawCluster = createRCFromCV(kernelVertex);
    		consumer.addCluster(rawCluster);
		}
	}

    /**
     * This method converts internal algorithm representation of vertices
     * to clusters suitalbe for displaying later.
     * @param vertex
     * @return cluster
     */
	private RawClusterBase createRCFromCV(CombinedVertex vertex) {
		RawClusterBase rawCluster = new RawClusterBase();

		RawDocument rawDocument;
		TokenizedDocument tokenizedDoc;		
		
		rawCluster.addLabel(vertex.getVertexDescription(params));
		
		ExtendedBitSet docSet = vertex.getDocuments();
		//for all documents in node(cluster)
		for (Iterator doc = docSet.iterator(); doc.hasNext();){
			final int docIndex = ((Integer) doc.next()).intValue();
			tokenizedDoc = (TokenizedDocument) documents.get(docIndex);
	        rawDocument = (RawDocument) tokenizedDoc.getProperty(
	        		TokenizedDocument.PROPERTY_RAW_DOCUMENT);
        	rawCluster.addDocument(rawDocument);
		}

		List successors = vertex.getSuccList();
		RawClusterBase subCluster;
		CombinedVertex successor;
		CombinedVertex sibling;
		
		boolean belongsToKernel;
		//for all children of this node
		for (int i1=0; i1<successors.size(); i1++){
			belongsToKernel = true;
			successor = (CombinedVertex) successors.get(i1);
			//Check if vertex isn't also grandchild =
			//Check if vertex belongs to kernel of subgraph
			for (int i2=0; i2<successors.size(); i2++) {
				sibling = (CombinedVertex) successors.get(i2); 
				if (sibling.getSuccList().contains(successor)){
					belongsToKernel = false;
				}
			}
			
			if (belongsToKernel){
				subCluster = createRCFromCV(successor);
				rawCluster.addSubcluster(subCluster);
			}
		}
		return rawCluster;
	}

}
