/**
 * 
 * @author chilang
 * Created 2003-12-29, 18:53:11.
 */
package com.chilang.carrot.filter.cluster;

import com.chilang.carrot.filter.cluster.rough.Snippet;
import com.chilang.carrot.filter.cluster.rough.clustering.Cluster;
import com.chilang.carrot.filter.cluster.rough.data.Term;
import com.chilang.util.JDOMWrapper;
import org.jdom.Element;

import java.text.NumberFormat;
import java.util.*;


/**
 * Wrapper for clustering results (collection of clusters)
 * to produce Carrot-XML
 *
 */
public class ClusterWrapper implements JDOMWrapper{

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
        this.root = new Element("");
    }
    /**
     * Construct a wrapper for a set of clusters under given element
     * @param clusters set of clusters
     * @param root JDOM element under which cluster elements will be exported
     */
    public ClusterWrapper(Cluster[] clusters, Element root) {
        this.clusters = clusters;
        this.root = root;
    }

    public Element asElement() {
        for(int i=0; i <clusters.length; i++) {
            root.addContent(convertToElement(clusters[i]));
        }
        return root;
    }

    private static Element convertToElement(Cluster cluster) {
        Element group = new Element("group");


		Element title = new Element("title");
		Map labels = extractTopPhrases(cluster.getRepresentativeTerm(), 3);
		if (labels != null)
		{

			for (Iterator k = labels.keySet().iterator(); k.hasNext(); )
			{
//                System.out.println(labels[k]);
				Element phrase = new Element("phrase");
                Term t = (Term) k.next();
				phrase.setText(t.getOriginalTerm() /*+"-"+numberFormat.format(labels[k].getWeight())*/);
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

		Snippet[] clusterDocuments = (Snippet[])cluster.getMembers();
		for (int k = 0; k < clusterDocuments.length; k++)
		{
			Element doc = new Element("document");
			doc.setAttribute("refid", String.valueOf(clusterDocuments[k].getId()));
			doc.setAttribute("score", numberFormat.format((cluster.getMembership().getWeight(clusterDocuments[k].getInternalId()))));
			group.addContent(doc);
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
