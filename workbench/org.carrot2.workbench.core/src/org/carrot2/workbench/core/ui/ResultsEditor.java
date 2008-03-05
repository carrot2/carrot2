package org.carrot2.workbench.core.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.MultiPageEditorPart;

public class ResultsEditor extends MultiPageEditorPart
{
    public static final String ID = "org.carrot2.workbench.core.editors.results";

    /**
     * Creates page 0 of the multi-page editor, which contains a text editor.
     */
    void createPage0()
    {
        Label l = new Label(getContainer(), SWT.NONE);
        l.setText("Editor!");
        addPage(l);
        setPageText(0, "Cluster Tree");
    }

    protected void createPages()
    {
        createPage0();
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
        return "Results";
    }

    @Override
    public String getTitleToolTip()
    {
        return "Results";
    }
}
