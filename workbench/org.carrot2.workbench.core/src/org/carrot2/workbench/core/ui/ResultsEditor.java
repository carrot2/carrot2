package org.carrot2.workbench.core.ui;

import java.util.Collection;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.workbench.core.helpers.ComponentLoader;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.*;
import org.eclipse.ui.part.MultiPageEditorPart;

public class ResultsEditor extends MultiPageEditorPart
{
    public static final String ID = "org.carrot2.workbench.core.editors.results";

    private void createClustersPage(Collection<Cluster> clusters)
    {
        ClusterTreeComponent tree = new ClusterTreeComponent();
        addPage(tree.createControls(getContainer(), clusters).getTree());
    }

    private void createDocumentsPage(Collection<Document> documents)
    {
        Text l = new Text(getContainer(), SWT.MULTI | SWT.WRAP | SWT.READ_ONLY
            | SWT.V_SCROLL);

        StringBuilder builder = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        for (Document doc : documents)
        {
            builder.append(doc.getField(Document.TITLE));
            builder.append(" - ");
            builder.append(doc.getField(Document.CONTENT_URL));
            builder.append(newLine);
        }
        l.setText(builder.toString());
        addPage(l);
    }

    protected void createPages()
    {
        // TODO: this should be done outside of gui thread
        SearchParameters search = (SearchParameters) getEditorInput();

        final SimpleController controller = new SimpleController();
        ProcessingResult result = controller.process(search.getAttributes(),
            ComponentLoader.SOURCE_LOADER.getComponent(search.getSourceCaption()),
            ComponentLoader.ALGORITHM_LOADER.getComponent(search.getAlgorithmCaption()));
        createClustersPage(result.getClusters());
        createDocumentsPage(result.getDocuments());
        setPageText(1, "Documents");
        setPageText(0, "Clusters");
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        if (!(input instanceof SearchParameters)) throw new PartInitException(
            "Invalid Input: Must be SearchParameters");

        super.init(site, input);
    }

    public void doSave(IProgressMonitor monitor)
    {
    }

    public void doSaveAs()
    {
    }

    public boolean isSaveAsAllowed()
    {
        return false;
    }

    @Override
    public String getPartName()
    {
        return ((SearchParameters) this.getEditorInput()).getAttributes().get(
            AttributeNames.QUERY).toString();
    }

    @Override
    public String getTitleToolTip()
    {
        return "Results";
    }
}
