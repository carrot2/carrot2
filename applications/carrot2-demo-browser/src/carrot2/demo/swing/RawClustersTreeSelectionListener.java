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
            final String html = tab.getHtmlForDocuments(rc.getDocuments(),
                    RawClustersCellRenderer.getLabel(rc, Integer.MAX_VALUE));
            tab.updateDocumentsView(html);
        }
    }
}
