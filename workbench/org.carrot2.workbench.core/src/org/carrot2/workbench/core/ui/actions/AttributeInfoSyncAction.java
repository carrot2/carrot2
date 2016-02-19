
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
import org.carrot2.workbench.core.ui.AttributeInfoView;
import org.carrot2.workbench.core.ui.PropertyChangeListenerAdapter;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * An action that synchronizes tooltip appearance with the {@link AttributeInfoView}'s
 * content.
 */
public final class AttributeInfoSyncAction extends ActionDelegate
{
    /*
     * 
     */
    private IAction action;

    /*
     * 
     */
    private final IPropertyChangeListener listener = 
        new PropertyChangeListenerAdapter(PreferenceConstants.ATTRIBUTE_INFO_SYNC)
    {
        protected void propertyChangeFiltered(PropertyChangeEvent event)
        {
            updateState();
        }
    };
    
    /*
     * 
     */
    @Override
    public void init(IAction action)
    {
        super.init(action);

        this.action = action;
        getPreferenceStore().addPropertyChangeListener(listener);
        updateState();
    }

    /*
     * 
     */
    private IPreferenceStore getPreferenceStore()
    {
        return WorkbenchCorePlugin.getDefault().getPreferenceStore();        
    }

    /*
     * 
     */
    @Override
    public void run(IAction action)
    {
        final IPreferenceStore preferenceStore = getPreferenceStore();
        preferenceStore.setValue(PreferenceConstants.ATTRIBUTE_INFO_SYNC, 
            !preferenceStore.getBoolean(PreferenceConstants.ATTRIBUTE_INFO_SYNC));
    }

    /*
     * 
     */
    private void updateState()
    {
        action.setChecked(getPreferenceStore().getBoolean(PreferenceConstants.ATTRIBUTE_INFO_SYNC));        
    }

    /*
     * 
     */
    public void dispose()
    {
        getPreferenceStore().removePropertyChangeListener(listener);
    }
}
