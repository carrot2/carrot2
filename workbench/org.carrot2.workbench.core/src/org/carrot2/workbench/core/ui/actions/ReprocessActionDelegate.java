package org.carrot2.workbench.core.ui.actions;

import org.carrot2.workbench.core.ui.SearchEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * Restarts processing in the currently active editor.
 */
public class ReprocessActionDelegate extends ActionDelegate implements
    IWorkbenchWindowActionDelegate, IEditorActionDelegate, IPropertyListener
{
    private SearchEditor editor;
    private IAction action;

    /*
     * 
     */
    public void setActiveEditor(IAction action, IEditorPart targetEditor)
    {
        if (this.action == null)
        {
            this.action = action;
        }

        if (this.action != action)
        {
            throw new RuntimeException("Multiple actions assigned to the delegate.");
        }

        unregister();

        if (targetEditor instanceof SearchEditor)
        {
            this.editor = (SearchEditor) targetEditor;
            this.editor.addPropertyListener(this);
        }

        updateState(action);
    }

    /*
     * 
     */
    public void run(IAction action)
    {
        if (editor.isDirty())
        {
            editor.reprocess();
        }
    }

    public void dispose()
    {
        unregister();
    }

    /**
     * 
     */
    public void propertyChanged(Object source, int propId)
    {
        if (propId == IEditorPart.PROP_DIRTY && action != null)
        {
            updateState(action);
        }
    }

    /*
     * 
     */
    public void init(IWorkbenchWindow window)
    {
        // Do nothing.
    }

    /**
     * Unregister from editor's property listening queue.
     */
    private void unregister()
    {
        if (editor != null)
        {
            editor.removePropertyListener(this);
            editor = null;
        }
    }

    /*
     * 
     */
    private void updateState(IAction action)
    {
        action.setEnabled(this.editor != null && this.editor.isDirty());
    }
}