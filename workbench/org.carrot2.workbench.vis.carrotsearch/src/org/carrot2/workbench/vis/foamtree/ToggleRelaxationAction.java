package org.carrot2.workbench.vis.foamtree;

import org.carrot2.workbench.core.ui.PropertyChangeListenerAdapter;
import org.carrot2.workbench.vis.Activator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Enable automatic relaxation toolbar button. 
 */
class ToggleRelaxationAction extends Action
{
    final static String RELAXATION_ENABLED_KEY = "relaxation-enabled";

    private IPropertyChangeListener listener = new PropertyChangeListenerAdapter(RELAXATION_ENABLED_KEY)
    {
        protected void propertyChangeFiltered(PropertyChangeEvent event)
        {
            updateState();
        }
    };

    /**
     * 
     */
    ToggleRelaxationAction()     
    {
        super(null, IAction.AS_CHECK_BOX);

        setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.ID, "icons/enabled/suspend_co.gif"));
        setDisabledImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.ID, "icons/disabled/suspend_co.gif"));
        setToolTipText("Pause relaxation");

        Activator.getInstance().getPreferenceStore().addPropertyChangeListener(listener);
        updateState();
    }
    
    @Override
    public void runWithEvent(Event event)
    {
        IPreferenceStore store = Activator.getInstance().getPreferenceStore();
        store.setValue(RELAXATION_ENABLED_KEY, !store.getBoolean(RELAXATION_ENABLED_KEY));
        updateState();
    }

    /**
     * 
     */
    protected void updateState()
    {
        setChecked(getCurrent());
    }

    /**
     * Return the current state of this action.
     */
    public static boolean getCurrent()
    {
        return Activator.getInstance().getPreferenceStore().getBoolean(RELAXATION_ENABLED_KEY);
    }
}
