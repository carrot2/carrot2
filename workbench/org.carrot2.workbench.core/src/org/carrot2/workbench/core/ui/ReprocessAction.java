package org.carrot2.workbench.core.ui;

import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;

/**
 * Restarts processing in the currently active editor.
 */
public final class ReprocessAction extends Action
{
    public ReprocessAction()
    {
        setImageDescriptor(WorkbenchCorePlugin.getImageDescriptor("icons/refresh_e.gif"));
        setDisabledImageDescriptor(WorkbenchCorePlugin.getImageDescriptor("icons/refresh_d.gif"));
    }

    /**
     * 
     */
    @Override
    public void run()
    {
        if (!isHandled())
            return;

        final SearchEditor searchResultsEditor = (SearchEditor) Utils.getActiveEditor();
        
        if (searchResultsEditor.isDirty())
        {
            searchResultsEditor.reprocess();
        }
    }

    /*
     * 
     */
    @Override
    public boolean isHandled()
    {
        final IEditorPart editor = Utils.getActiveEditor();
        return editor != null && (editor instanceof SearchEditor);
    }

    /*
     * 
     */
    @Override
    public String getToolTipText()
    {
        return "Refresh";
    }
}
