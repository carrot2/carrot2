package org.carrot2.workbench.core.ui.actions;

import org.carrot2.workbench.core.ui.SearchEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * Controls the state of the auto-update feature for editors (re-processing
 * after attributes change). 
 */
public class AutoUpdateActionDelegate extends ActionDelegate implements
    IWorkbenchWindowActionDelegate, IEditorActionDelegate, IPropertyListener
{
    private SearchEditor editor;
    private IAction action;

    /*
     * TODO: Currently auto-update is a property of an editor. In my view it should
     * be a global setting, though (who'd want to keep separate states for multiple
     * editors)?
     */

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
        if (editor != null)
        {
            editor.setAutoUpdate(!editor.isAutoUpdate());
            updateState(action);
        }
    }

    /*
     * 
     */
    public void dispose()
    {
        unregister();
    }

    /**
     * 
     */
    public void propertyChanged(Object source, int propId)
    {
        if (propId == SearchEditor.PROP_AUTO_UPDATE && action != null)
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
        action.setEnabled(this.editor != null);
        
        boolean checked = false;
        if (this.editor != null && editor.isAutoUpdate())
        {
            checked = true;
        }
        action.setChecked(checked);
    }
}