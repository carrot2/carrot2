package org.carrot2.workbench.core.ui.actions;

import org.carrot2.workbench.core.ui.SearchEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;

/**
 * Controls the state of the auto-update feature for editors (re-processing
 * after attributes change). 
 */
public class AutoUpdateActionDelegate extends ActiveSearchEditorActionDelegate 
{
    /*
     * 
     */
    @Override
    public void run(SearchEditor editor)
    {
        editor.setAutoUpdate(!editor.isAutoUpdate());
    }
    
    @Override
    protected void updateActionState(IAction action, IEditorPart editor)
    {
        super.updateActionState(action, editor);
        action.setChecked(((SearchEditor) editor).isAutoUpdate());
    }
}