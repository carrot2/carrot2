
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

package com.stachoodev.carrot.filter.lingo;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import com.stachoodev.carrot.filter.lingo.common.AbstractClusteringContext;
import com.stachoodev.carrot.filter.lingo.common.Cluster;
import com.stachoodev.carrot.filter.lingo.common.Snippet;


/**
 * Helper class for cluster-serialized form operations.
 */
class ClusterStructureHelpers {

    private static final NumberFormat numberFormat;

    static {
        numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
    }

    private ClusterStructureHelpers() {
    }

    /**
     * @param clusteringContext
     * @param documentList
     */
    public static void addSnippets(
        AbstractClusteringContext clusteringContext, List documentList) {
        for (Iterator i = documentList.iterator(); i.hasNext();) {
            Element document = (Element) i.next();

            String title = document.elementText("title");
            String snippet = document.elementText("snippet");

            clusteringContext.addSnippet(new Snippet(document.attributeValue(
                        "id"), title, snippet));
        }
    }

    /**
     * @param rootGroup
     * @param cluster
     */
    public static void addToElement(Element rootGroup, Cluster cluster) {
        if (cluster.getScore() == 0) {
            return;
        }

        final DocumentFactory factory = new DocumentFactory();
        Element group = factory.createElement("group");

        if (cluster.isOtherTopics()) {
            group.addAttribute("othertopics", "yes");
        }

        Element title = factory.createElement("title");
        String[] labels = cluster.getLabels();

        if (labels != null) {
            for (int k = 0; k < labels.length; k++) {
                Element phrase = factory.createElement("phrase");
                phrase.setText(labels[k]);
                title.add(phrase);
            }
        } else {
            Element phrase = factory.createElement("phrase");
            phrase.setText("Group");
            title.add(phrase);
        }

        group.add(title);

        Snippet[] clusterDocuments = cluster.getSnippets();

        for (int k = 0; k < clusterDocuments.length; k++) {
            Element doc = factory.createElement("document");
            doc.addAttribute("refid", clusterDocuments[k].getSnippetId());
            doc.addAttribute("score",
                numberFormat.format(cluster.getSnippetScore(k)));
            group.add(doc);
        }

        Cluster[] clusters = cluster.getClusters();

        for (int i = 0; i < clusters.length; i++) {
            addToElement(group, clusters[i]);
        }

        rootGroup.add(group);
    }
}
