
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
