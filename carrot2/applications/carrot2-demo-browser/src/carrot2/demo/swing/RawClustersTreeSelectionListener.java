
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package carrot2.demo.swing;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.dawidweiss.carrot.core.local.clustering.RawCluster;

/**
 * A tree selection listener that displays cluster content
 * in a HTML view.
 * 
 * @author Dawid Weiss
 */
public class RawClustersTreeSelectionListener implements TreeSelectionListener {

    private ResultsTab tab;

    public RawClustersTreeSelectionListener(ResultsTab tab) {
        this.tab = tab;
    }

    public void valueChanged(TreeSelectionEvent tse) {
        Object selected = ((JTree) tse.getSource()).getLastSelectedPathComponent();
        if (selected instanceof RawCluster) {
            // change documents view.
            final RawCluster rc = (RawCluster) selected;
            final String html = tab.getHtmlFor(rc);
            tab.updateDocumentsView(html);
        }
    }
}
