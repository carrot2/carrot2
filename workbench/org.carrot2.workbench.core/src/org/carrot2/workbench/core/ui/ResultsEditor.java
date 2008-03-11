package org.carrot2.workbench.core.ui;

import java.util.Collection;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.workbench.core.CorePlugin;
import org.carrot2.workbench.core.helpers.ComponentLoader;
import org.carrot2.workbench.core.helpers.RunnableWithErrorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.part.MultiPageEditorPart;

public class ResultsEditor extends MultiPageEditorPart
{
    public static final String ID = "org.carrot2.workbench.core.editors.results";

    /*
     * 
     */
    private void createClustersPage(Collection<Cluster> clusters)
    {
        ClusterTreeComponent tree = new ClusterTreeComponent();
        setControl(0, tree.createControls(getContainer(), clusters).getTree());
    }

    /*
     * 
     */
    private void createDocumentsPage(Collection<Document> documents)
    {
        DocumentListComponent list = new DocumentListComponent();
        setControl(1, list.createControls(getContainer(), documents));
    }

    /*
     * 
     */
    private void performClustering()
    {
        CorePlugin.getExecutorService().execute(new RunnableWithErrorDialog()
        {
            public void runCore()
            {
                SearchParameters search = (SearchParameters) getEditorInput();

                final SimpleController controller = new SimpleController();
                final ProcessingResult result = controller.process(
                    search.getAttributes(), ComponentLoader.SOURCE_LOADER
                        .getComponent(search.getSourceCaption()),
                    ComponentLoader.ALGORITHM_LOADER.getComponent(search
                        .getAlgorithmCaption()));
                buildPages(result);

            }

            @Override
            protected String getErrorTitle()
            {
                return "Error while processing query";
            }
        });
    }

    /*
     * } } }); } /*
     * 
     */
    private void buildPages(final ProcessingResult result)
    {
        Display.getDefault().asyncExec(new RunnableWithErrorDialog()
        {
            public void runCore()
            {
                createClustersPage(result.getClusters());
                createDocumentsPage(result.getDocuments());
            }

            @Override
            protected String getErrorTitle()
            {
                return "Error while creating results editor";
            }

        });
    }

    /*
     * 
     */
    protected void createPages()
    {
        /*
         * [dw] I don't want to get in the way here, but wouldn't a classic MVC pattern be
         * better for the design of this class/ functionality? What I specifically mean is
         * to have a model object wrapping the clustering to be executed and implementing
         * a listener/ event producer pattern.
         * 
         * Then you can create composites that are attached to the above model and listen
         * for events about the clustering being in progress, updated or finished in which
         * case they re-render their content (if they are visible).
         * 
         * Just a thought, but certainly something to think of.
         */
        performClustering();
        addPage(null);
        addPage(null);
        setPageText(1, "Documents");
        setPageText(0, "Clusters");
    }

    /*
     * 
     */
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        if (!(input instanceof SearchParameters)) throw new PartInitException(
            "Invalid Input: Must be SearchParameters");

        super.init(site, input);
    }

    /*
     * 
     */
    public void doSave(IProgressMonitor monitor)
    {
    }

    /*
     * 
     */
    public void doSaveAs()
    {
    }

    /*
     * 
     */
    public boolean isSaveAsAllowed()
    {
        return false;
    }

    /*
     * 
     */
    @Override
    public String getPartName()
    {
        return ((SearchParameters) this.getEditorInput()).getAttributes().get(
            AttributeNames.QUERY).toString();
    }

    /*
     * 
     */
    @Override
    public String getTitleToolTip()
    {
        return "Results";
    }
}
