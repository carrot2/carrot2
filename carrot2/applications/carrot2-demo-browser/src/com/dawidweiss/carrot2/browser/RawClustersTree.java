package com.dawidweiss.carrot2.browser;

import javax.swing.BorderFactory;
import javax.swing.JTree;
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
        putClientProperty("JTree.lineStyle", "None");
        setCellRenderer(new RawClustersCellRenderer());
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setEditable(false);        
    }
}
