package org.carrot2.workbench.core.ui;

import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.workbench.core.CorePlugin;
import org.carrot2.workbench.core.jobs.ProcessingJob;
import org.carrot2.workbench.core.ui.clusters.ClusterTreeComponent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;

public class ResultsEditor extends SashFormEditorPart
{
    public static final String ID = "org.carrot2.workbench.core.editors.results";

    @Override
    protected void createControls()
    {
        ProcessingJob job =
            new ProcessingJob("Processing of a query",
                (SearchParameters) getEditorInput());
        ClusterTreeComponent tree =
            new ClusterTreeComponent(getSite(), getContainer(), job);
        addControl(tree.getControl(), 1);
        DocumentListBrowser browser =
            new DocumentListBrowser(getSite(), getContainer(), job);
        addControl(browser.getControl(), 2);
        CorePlugin.getDefault().getWorkbench().getProgressService().showInDialog(
            Display.getDefault().getActiveShell(), job);
        job.schedule();
    }

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

    @Override
    public void setFocus()
    {
    }

    @Override
    public boolean isDirty()
    {
        return false;
    }
}
