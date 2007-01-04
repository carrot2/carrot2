
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

package org.carrot2.filter.trc.carrot.filter.cluster;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import org.carrot2.filter.trc.carrot.filter.cluster.rough.Snippet;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.clustering.Cluster;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.data.Term;


/**
 * Wrapper for clustering results (collection of clusters)
 * to produce Carrot-XML
 */
public class ClusterWrapper {

    private static final NumberFormat numberFormat;
    static {
        numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
    }

    Cluster[] clusters;
    Element root;

    /**
     * Construct a wrapper for a set of clusters
     * @param clusters
     */
    public ClusterWrapper(Cluster[] clusters) {
        this.clusters = clusters;
        this.root = new DocumentFactory().createElement("searchresult");
    }

    /**
     * Construct a wrapper for a set of clusters under given element
     * @param clusters set of clusters
     * @param root DOM4J element under which cluster elements will be exported
     */
    public ClusterWrapper(Cluster[] clusters, Element root) {
        this.clusters = clusters;
        this.root = root;
    }

    public Element asElement() {
        for(int i=0; i < clusters.length; i++) {
            root.add(convertToElement(clusters[i]));
        }
        return root;
    }

    private static Element convertToElement(Cluster cluster) {
        final DocumentFactory factory = new DocumentFactory();
        Element group = factory.createElement("group");
		Element title = factory.createElement("title");
		Map labels = extractTopPhrases(cluster.getRepresentativeTerm(), 3);
		if (labels != null)
		{
			for (Iterator k = labels.keySet().iterator(); k.hasNext(); )
			{
				Element phrase = factory.createElement("phrase");
                Term t = (Term) k.next();
				phrase.setText(t.getOriginalTerm() /*+"-"+numberFormat.format(labels[k].getWeight())*/);
				title.add(phrase);
			}
		}
		else
		{
			Element phrase = factory.createElement("phrase");
			phrase.setText("Group");
			title.add(phrase);
		}
		group.add(title);

		Snippet[] clusterDocuments = (Snippet[])cluster.getMembers();
		for (int k = 0; k < clusterDocuments.length; k++)
		{
			Element doc = factory.createElement("document");
			doc.addAttribute("refid", String.valueOf(clusterDocuments[k].getId()));
			doc.addAttribute("score", numberFormat.format((cluster.getMembership().getWeight(clusterDocuments[k].getInternalId()))));
			group.add(doc);
		}
        return group;
    }

    private static Map extractTopPhrases(final Map phrares, int noOfPhrares) {
        SortedSet top = new TreeSet(new Comparator() {
            public int compare(Object o1, Object o2) {
                if (!((o1 instanceof Term) && (o2 instanceof Term)))
                    throw new IllegalArgumentException("Argument must be of class " + Term.class);

                return (((Double)phrares.get(o1)).doubleValue() > ((Double)phrares.get(o2)).doubleValue()) ? -1 : 1;

            }
        });
        top.addAll(phrares.keySet());

        Map topPhrases = new HashMap();

        int size = noOfPhrares > top.size() ? top.size() : noOfPhrares;
        int c = 0;
        for (Iterator i = top.iterator(); i.hasNext() && c < size; c++) {
            Term term = (Term) i.next();
            topPhrases.put(term, phrares.get(term));
        }

        return topPhrases;
    }
}
