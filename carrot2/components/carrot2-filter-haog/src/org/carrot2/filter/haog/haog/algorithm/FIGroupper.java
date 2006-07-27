
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

package org.carrot2.filter.haog.haog.algorithm;

import java.util.List;

import com.dawidweiss.carrot.core.local.clustering.RawClustersConsumer;

public class FIGroupper extends Groupper {
	
	public FIGroupper(List clusters, List documents, Object paramters, 
	RawClustersConsumer consumer){
		this.clusters = clusters;
		this.documents = documents;
		this.parameters = paramters;
		this.consumer = consumer;
		this.creator = new FIGraphCreator();
		this.renderer = new FIGraphRenderer();
	}
}
