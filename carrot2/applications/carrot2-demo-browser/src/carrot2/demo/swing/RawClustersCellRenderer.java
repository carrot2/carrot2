package carrot2.demo.swing;

import java.awt.*;
import java.text.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.*;

import com.dawidweiss.carrot.core.local.clustering.*;

public class RawClustersCellRenderer extends DefaultTreeCellRenderer {
    private final static int MAX_CLUSTER_DESCRIPTION_WIDTH = 80;

    private static Icon clusterIconJunk = new ImageIcon(RawClustersCellRenderer.class.getResource("junk-cluster.gif"));

    private static Icon subclustersIconJunk = new ImageIcon(
            RawClustersCellRenderer.class.getResource("junk-cluster-subclusters.gif"));

    private static Icon clusterIcon = new ImageIcon(RawClustersCellRenderer.class.getResource("cluster.gif"));

    private static Icon subclustersIcon = new ImageIcon(RawClustersCellRenderer.class
            .getResource("cluster-subclusters.gif"));

    /**
     * 
     */
    static final String PROPERTY_SEARCH_MATCHES = "contains-search-term";

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, "", selected, expanded, leaf, row, hasFocus);

        if (value instanceof RawCluster) {
            final RawCluster rc = (RawCluster) value;
            String label = getLabel(rc, MAX_CLUSTER_DESCRIPTION_WIDTH);

            List subs = rc.getSubclusters();
            if (rc.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) != null) {
                if (subs == null || subs.size() > 0) {
                    setIcon(subclustersIconJunk);
                } else {
                    setIcon(clusterIconJunk);
                }
            } else {
                if (subs == null || subs.size() > 0) {
                    setIcon(subclustersIcon);
                } else {
                    setIcon(clusterIcon);
                }
            }
            
            final Integer searchMatches = (Integer) rc.getProperty(PROPERTY_SEARCH_MATCHES);
            if (searchMatches != null)
            {
                setFont(new Font(getFont().getName(), Font.BOLD, getFont().getSize()));
                label += " {" + searchMatches.toString() + "}";
            }
            else
            {
                setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()));
            }
            setText(label);

            final String toolTipText = createToolTipText((RawCluster) value);
            if (toolTipText.trim().length() > 0) {
                setToolTipText(toolTipText);
            }
        } else if (value instanceof String) {
            setText((String) value);
        }

        return this;
    }

    public static String getLabel(RawCluster rc, final int maxClusterDescriptionWidth) {
        List description = rc.getClusterDescription();

        StringBuffer buf = new StringBuffer();
        for (Iterator i = description.iterator(); i.hasNext();) {
            String phrase = (String) i.next();

            if ((buf.length() + phrase.length()) > maxClusterDescriptionWidth) {
                buf.append(phrase.substring(0, maxClusterDescriptionWidth - buf.length()));
                buf.append("...");
                break;
            } else {
                if (buf.length() > 0) {
                    buf.append("; ");
                }

                buf.append(phrase);
            }
        }

//        buf.append(" (");
//        buf.append(getClusterSize(rc));
//        buf.append(')');

        buf.append(" (");
        buf.append(getUniqueClusterSize(rc));
        buf.append(')');

        return buf.toString();
    }

    private static int getClusterSize(RawCluster rawCluster) {
        int size = rawCluster.getDocuments().size();

        if (rawCluster.getSubclusters() != null) {
            for (Iterator iter = rawCluster.getSubclusters().iterator(); iter.hasNext();) {
                RawCluster subcluster = (RawCluster) iter.next();
                size += getClusterSize(subcluster);
            }
        }

        return size;
    }

    private static int getUniqueClusterSize(RawCluster rawCluster) {
        Set documents = new HashSet(200);
        addDocuments(rawCluster, documents);
        return documents.size();
    }

    private static void addDocuments(RawCluster rawCluster, Set documents) {
        documents.addAll(rawCluster.getDocuments());
        if (rawCluster.getSubclusters() != null) {
            for (Iterator iter = rawCluster.getSubclusters().iterator(); iter.hasNext();) {
                RawCluster subcluster = (RawCluster) iter.next();
                addDocuments(subcluster, documents);
            }
        }
    }

    private static String createToolTipText(RawCluster rawCluster) {
        StringBuffer toolTipText = new StringBuffer();

        toolTipText.append("<html><body>");

        // ClusterMetadata.PROPERTY_LABEL_SYNONYMS
        List synonymLabels = (List) rawCluster.getProperty("label-synonyms");
        List synonymScores = (List) rawCluster.getProperty("label-synonym-scores");

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);

        toolTipText.append("<b>cluster score: ");
        toolTipText.append(numberFormat.format(((Double) rawCluster.getProperty(RawCluster.PROPERTY_SCORE))
                .doubleValue()));
        toolTipText.append("</b><br>");

        if (synonymLabels != null) {
            for (int i = 0; i < synonymLabels.size(); i++) {
                String label = (String) synonymLabels.get(i);
                Double score = (Double) synonymScores.get(i);

                toolTipText.append(label);
                toolTipText.append(" (");
                toolTipText.append(numberFormat.format(score.doubleValue()));
                toolTipText.append(")");
                if (i != synonymLabels.size() - 1) {
                    toolTipText.append("<br>");
                }
            }
        }
        toolTipText.append("</body></html>");

        return toolTipText.toString();
    }
}