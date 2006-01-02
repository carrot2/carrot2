
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
package com.chilang.carrot.filter.cluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

import com.chilang.carrot.filter.cluster.rough.clustering.Cluster;
import com.chilang.carrot.filter.cluster.rough.data.SnippetDocument;



/**
 * Base class for LSI-based clustering filters.
 * Imported from Carrot 2 project.
 */
public abstract class AbstractClusteringRequestProcessor
        extends com.dawidweiss.carrot.filter.FilterRequestProcessor
{
	public AbstractClusteringRequestProcessor()
    {
    }

    //TODO change to SAX parsing, so incremental processing is possible
    protected Collection getSnippets(List documentList) {
        Collection snips = new ArrayList();
        for (Iterator i = documentList.iterator(); i.hasNext(); )
		{
			Element document = (Element) i.next();

            SnippetDocument doc = new SnippetDocument(document.attributeValue("id"));
            doc.setTitle(document.elementText("title"));
            doc.setUrl(document.elementText("url"));
            doc.setDescription(document.elementText("snippet"));
			snips.add(doc);
		}
        return snips;
    }


    /**
     * Add clustering results (clusters) under given DOM root element.
     * The resulting/modified element is returned.
     */
    protected void addClusteringResult(Element root, Cluster[] clusters) {
        new ClusterWrapper(clusters, root).asElement();
    }
}
