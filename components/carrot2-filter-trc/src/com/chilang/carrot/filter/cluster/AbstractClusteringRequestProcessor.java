/**
 * 
 * @author chilang
 * Created 2003-07-25, 23:39:52.
 */
package com.chilang.carrot.filter.cluster;

import com.chilang.util.JDOMWrapper;
import com.chilang.carrot.filter.cluster.rough.clustering.Cluster;
import com.chilang.carrot.filter.cluster.rough.data.SnippetDocument;
import com.chilang.util.JDOMWrapper;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;



/**
 * Base class for LSI-based clustering filters.
 * Imported from Carrot 2 project.
 */
public abstract class AbstractClusteringRequestProcessor
        extends com.dawidweiss.carrot.filter.FilterRequestProcessor
{

    /**
	 *
	 */
	public AbstractClusteringRequestProcessor()
    {
        // Double formatter

    }

    //TODO change to SAX parsing, so incremental processing is possible
    protected Collection getSnippets(List documentList) {
        Collection snips = new ArrayList();
        for (Iterator i = documentList.iterator(); i.hasNext(); )
		{

			Element document = (Element) i.next();

            SnippetDocument doc = new SnippetDocument(document.getAttributeValue("id"));
            doc.setTitle(document.getChildText("title"));
            doc.setUrl(document.getChildText("url"));
            doc.setDescription(document.getChildText("snippet"));
			snips.add(doc);

		}
        return snips;
    }


    /**
     * Add clustering results (clusters) under given DOM root element.
     * The resulting/modified element is returned.
     * @param root
     * @param clusters
     */
    protected void addClusteringResult(Element root, Cluster[] clusters) {
        JDOMWrapper wrapper = new ClusterWrapper(clusters, root);
        root = wrapper.asElement();
    }


}
