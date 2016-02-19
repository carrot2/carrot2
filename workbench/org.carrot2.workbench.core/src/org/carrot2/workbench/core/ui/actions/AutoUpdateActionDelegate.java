
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui.actions;

import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.preferences.PreferenceConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * Controls the state of the auto-update feature for editors (re-processing
 * after attributes change). 
 */
public class AutoUpdateActionDelegate extends ActionDelegate implements IWorkbenchWindowActionDelegate
{
    /*
     * 
     */
    private IAction action;

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
                updateActionState();
            }
        }
    };

    /*
     * 
     */
    @Override
    public void run(IAction action)
    {
        store.setValue(PreferenceConstants.AUTO_UPDATE, 
            !store.getBoolean(PreferenceConstants.AUTO_UPDATE));
    }

    /*
     * 
     */
    @Override
    public void init(IAction action)
    {
        super.init(action);

        this.action = action;
        this.store = WorkbenchCorePlugin.getDefault().getPreferenceStore();
        store.addPropertyChangeListener(listener);

        updateActionState();
    }

    @Override
    public void dispose()
    {
        super.dispose();
        store.removePropertyChangeListener(listener);
    }

    /*
     * 
     */
    final void updateActionState()
    {
        action.setChecked(store.getBoolean(PreferenceConstants.AUTO_UPDATE));
    }

    /*
     * 
     */
    public void init(IWorkbenchWindow window)
    {
        // Empty.
    }
}
