

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.stachoodev.carrot.filter.cluster;


import com.stachoodev.carrot.filter.cluster.common.*;
import org.jdom.Element;
import java.text.NumberFormat;
import java.util.*;


/**
 * Base class for LSI-based clustering filters.
 */
public abstract class AbstractLsiClustererRequestProcessor
    extends com.dawidweiss.carrot.filter.FilterRequestProcessor
{
    /** */
    private NumberFormat numberFormat;

    /**
     *
     */
    public AbstractLsiClustererRequestProcessor()
    {
        // Double formatter
        numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
    }

    /**
     * @param clusteringContext
     * @param documentList
     */
    protected void addSnippets(AbstractClusteringContext clusteringContext, List documentList)
    {
        for (Iterator i = documentList.iterator(); i.hasNext();)
        {
            Element document = (Element) i.next();

            String title = document.getChildText("title");
            String snippet = document.getChildText("snippet");

            clusteringContext.addSnippet(
                new Snippet(document.getAttributeValue("id"), title, snippet)
            );
        }
    }


    /**
     * @param rootGroup
     * @param cluster
     */
    protected void addToElement(Element rootGroup, Cluster cluster)
    {
        if (cluster.getScore() == 0)
        {
            return;
        }

        Element group = new Element("group");

        if (cluster.isOtherTopics())
        {
            group.setAttribute("othertopics", "yes");
        }

        Element title = new Element("title");
        String [] labels = cluster.getLabels();

        if (labels != null)
        {
            for (int k = 0; k < labels.length; k++)
            {
                Element phrase = new Element("phrase");
                phrase.setText(labels[k]);
                title.addContent(phrase);
            }
        }
        else
        {
            Element phrase = new Element("phrase");
            phrase.setText("Group");
            title.addContent(phrase);
        }

        group.addContent(title);

        Snippet [] clusterDocuments = cluster.getSnippets();

        for (int k = 0; k < clusterDocuments.length; k++)
        {
            Element doc = new Element("document");
            doc.setAttribute("refid", clusterDocuments[k].getId());
            doc.setAttribute("score", numberFormat.format(cluster.getSnippetScore(k)));
            group.addContent(doc);
        }

        Cluster [] clusters = cluster.getClusters();

        for (int i = 0; i < clusters.length; i++)
        {
            addToElement(group, clusters[i]);
        }

        rootGroup.addContent(group);
    }
}
