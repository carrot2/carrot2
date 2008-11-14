
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.demo.swing;

import java.awt.Component;
import java.awt.Font;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.carrot2.core.clustering.RawCluster;
import org.carrot2.demo.ClusterInfoRenderer;

public class RawClustersCellRenderer extends DefaultTreeCellRenderer {
    private final static int MAX_CLUSTER_DESCRIPTION_WIDTH = 80;

    private static Icon clusterIconJunk = new ImageIcon(RawClustersCellRenderer.class.getResource("junk-cluster.gif"));

    private static Icon subclustersIconJunk = new ImageIcon(
            RawClustersCellRenderer.class.getResource("junk-cluster-subclusters.gif"));

    private static Icon clusterIcon = new ImageIcon(RawClustersCellRenderer.class.getResource("cluster.gif"));

    private static Icon subclustersIcon = new ImageIcon(RawClustersCellRenderer.class
            .getResource("cluster-subclusters.gif"));

    static final String PROPERTY_SEARCH_MATCHES = "contains-search-term";

    private ClusterInfoRenderer clusterInfoRenderer;
    
    public RawClustersCellRenderer()
    {
        this(null);
    }
    
    public RawClustersCellRenderer(ClusterInfoRenderer clusterInfoRenderer)
    {
        this.clusterInfoRenderer = clusterInfoRenderer;
    }
    
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
            
            if (clusterInfoRenderer != null)
            {
                final String prefix = clusterInfoRenderer
                    .getClusterLabelPrefix(rc);
                if (prefix != null)
                {
                    label = prefix + " " + label;
                }

                final String suffix = clusterInfoRenderer
                    .getClusterLabelSuffix(rc);
                if (suffix != null)
                {
                    label += " " + suffix;
                }
            }            
            setText(label);

            final String toolTipText = createToolTipText((RawCluster) value);
            if (toolTipText != null) {
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
            final String phrase = (String) i.next();
            if (buf.length() > 0) {
                buf.append("; ");
            }
            buf.append(phrase);

            if (buf.length() > maxClusterDescriptionWidth) {
                buf.setLength(maxClusterDescriptionWidth - "...".length());
                buf.append("...");
                break;
            }
        }

        buf.append(" (");
        buf.append(getUniqueClusterSize(rc));
        buf.append(')');

        return buf.toString();
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

    private String createToolTipText(RawCluster rawCluster) {
        
        if (clusterInfoRenderer != null)
        {
            StringBuffer toolTipText = new StringBuffer();
            toolTipText.append("<html><body>");
            toolTipText.append(clusterInfoRenderer.getHtmlClusterInfo(rawCluster));
            toolTipText.append("</body></html>");
            return toolTipText.toString();
        }
        else
        {
            return null;
        }
    }
}