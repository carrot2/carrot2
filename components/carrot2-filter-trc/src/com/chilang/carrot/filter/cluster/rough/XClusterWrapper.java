/**
 * 
 * @author chilang
 * Created 2003-12-29, 18:53:11.
 */
package com.chilang.carrot.filter.cluster.rough;

import com.chilang.util.JDOMWrapper;
import com.chilang.carrot.filter.cluster.rough.clustering.XCluster;
import org.jdom.Element;

import java.text.NumberFormat;
import java.util.Locale;


/**
 * Wrapper for clustering results (collection of clusters)
 * to produce Carrot-XML
 *
 */
public class XClusterWrapper implements JDOMWrapper {

    private static final NumberFormat numberFormat;

    static {
        numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
    }

    XCluster[] clusters;
    Element root;
    
    /**
     * Construct a wrapper for a set of clusters under given element
     * @param clusters set of clusters
     * @param root JDOM element under which cluster elements will be exported
     */
    public XClusterWrapper(XCluster[] clusters, Element root) {
        this.clusters = clusters;
        this.root = root;
    }

    public Element asElement() {
        for (int i = 0; i < clusters.length; i++) {
            root.addContent(convertToElement(clusters[i]));
        }
        return root;
    }

    private static Element convertToElement(XCluster cluster) {
        Element group = new Element("group");


        Element title = new Element("title");
        String[] labels = cluster.getLabel();
        if (labels != null) {

            for (int i = 0; i < labels.length; i++) {
//                System.out.println(labels[k]);
                Element phrase = new Element("phrase");
                phrase.setText(labels[i]);
                title.addContent(phrase);
            }
        } else {
            Element phrase = new Element("phrase");
            phrase.setText("Group");
            title.addContent(phrase);
        }
        group.addContent(title);

        XCluster.Member[] members = cluster.getMembers();
        for (int k = 0; k < members.length; k++) {
            Element doc = new Element("document");
            doc.setAttribute("refid", members[k].getSnippet().getId());
            doc.setAttribute("score", numberFormat.format(members[k].getMembership()));
            group.addContent(doc);
        }
        return group;
    }

}
