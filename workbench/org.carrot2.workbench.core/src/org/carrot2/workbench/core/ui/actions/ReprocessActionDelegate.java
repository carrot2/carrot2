package org.carrot2.workbench.core.ui.actions;

import org.carrot2.workbench.core.ui.SearchEditor;
import org.eclipse.ui.IEditorPart;

/**
 * Restarts processing in the currently active editor.
 */
public class ReprocessActionDelegate extends ActiveSearchEditorActionDelegate 
{
    /*
     * 
     */
    @Override
    public void run(SearchEditor editor)
    {
        editor.reprocess();
    }

    /**
     * Is this action enabled for the given editor?
     */
    protected boolean isEnabled(IEditorPart activeEditor)
    {
        /*
         * Allow forcing of re-rendering of the editor's contents when 
         * auto-update is off. Alternatively, we could disable re-rendering with:
         *  
         * activeEditor.isDirty();
         */
        return !((SearchEditor) activeEditor).isAutoUpdate();
    }
}