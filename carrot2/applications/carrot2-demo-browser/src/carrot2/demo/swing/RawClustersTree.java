package carrot2.demo.swing;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * A tree of clusters
 * @author Dawid Weiss
 */
public class RawClustersTree extends JTree {
    public RawClustersTree() {
        initializeGui();
    }
    
    private void initializeGui() {
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Wait, processing...")));
        
        setShowsRootHandles(true);
        setRootVisible(false);
        setCellRenderer(new RawClustersCellRenderer());
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setEditable(false);        
        ToolTipManager.sharedInstance().registerComponent(this);
    }
}
