package carrot2.demo.swing;

import java.awt.Component;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.dawidweiss.carrot.core.local.clustering.RawCluster;


public class RawClustersCellRenderer extends DefaultTreeCellRenderer {
    private final static int MAX_CLUSTER_DESCRIPTION_WIDTH = 30;
    
    private static Icon clusterIconJunk = new ImageIcon(
            RawClustersCellRenderer.class.getResource("junk-cluster.gif"));
    private static Icon subclustersIconJunk = new ImageIcon(
            RawClustersCellRenderer.class.getResource("junk-cluster-subclusters.gif"));
    private static Icon clusterIcon = new ImageIcon(
            RawClustersCellRenderer.class.getResource("cluster.gif"));
    private static Icon subclustersIcon = new ImageIcon(
            RawClustersCellRenderer.class.getResource("cluster-subclusters.gif"));

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, "", selected, expanded, leaf, row, hasFocus);
        
        if (value instanceof RawCluster) {
            RawCluster rc = (RawCluster) value;
            List description = rc.getClusterDescription();

            StringBuffer buf = new StringBuffer();
            for (Iterator i = description.iterator(); i.hasNext();) {
                String phrase = (String) i.next();

                if ((buf.length() + phrase.length()) > MAX_CLUSTER_DESCRIPTION_WIDTH) {
                    buf.append(phrase.substring(0,
                            MAX_CLUSTER_DESCRIPTION_WIDTH - buf.length()));
                    buf.append("...");
                    break;
                } else {
                    if (buf.length() > 0) {
                        buf.append("; ");
                    }

                    buf.append(phrase);
                }
            }

            List subs = rc.getSubclusters();
            if (rc.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) != null) {
	            if (subs==null || subs.size() > 0) {
	                setIcon(subclustersIconJunk);
	            } else {
	                setIcon(clusterIconJunk);                
	            }
            } else {
	            if (subs==null || subs.size() > 0) {
	                setIcon(subclustersIcon);
	            } else {
	                setIcon(clusterIcon);                
	            }
            }
            setText(buf.toString());
        } else if (value instanceof String) {
            setText( (String) value );
        }

        return this;
    }
}