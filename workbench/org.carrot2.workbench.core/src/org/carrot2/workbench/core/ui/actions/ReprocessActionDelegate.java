
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui.actions;

import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.preferences.PreferenceConstants;
import org.carrot2.workbench.core.ui.SearchEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorPart;

/**
 * Restarts processing in the currently active editor.
 */
public class ReprocessActionDelegate extends ActiveSearchEditorActionDelegate 
{
    /*
     * 
     */
    private IPreferenceStore store;

    /**
     * When auto-update key in the preference store changes, update the state
     * of this action.
     */
    private IPropertyChangeListener listener = new IPropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event)
        {
            if (PreferenceConstants.AUTO_UPDATE.equals(event.getProperty()))
            {
                updateActionState(getAction(), getEditor());
            }
        }
    };

    @Override
    public void init(IAction action)
    {
        this.store = WorkbenchCorePlugin.getDefault().getPreferenceStore();
        store.addPropertyChangeListener(listener);

        super.init(action);
    }
    
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

        final IPreferenceStore store = WorkbenchCorePlugin.getDefault().getPreferenceStore();
        final boolean autoUpdate = store.getBoolean(PreferenceConstants.AUTO_UPDATE);

        return activeEditor != null && !autoUpdate;
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        store.removePropertyChangeListener(listener);
    }
}
