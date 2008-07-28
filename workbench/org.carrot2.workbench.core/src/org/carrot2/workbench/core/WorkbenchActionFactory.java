package org.carrot2.workbench.core;

import org.carrot2.workbench.core.helpers.ActionDelegateProxy;
import org.carrot2.workbench.core.ui.actions.AutoUpdateActionDelegate;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Static {@link ActionFactory} instances for common actions (that cannot be declaratively
 * added to certain elements of the GUI).
 */
public class WorkbenchActionFactory
{
    /**
     * Auto update action.
     */
    public static final ActionFactory AUTO_UPDATE_ACTION = new ActionFactory("auto-update") {
        @Override
        public IWorkbenchAction create(IWorkbenchWindow window)
        {
            final IWorkbenchAction action = new ActionDelegateProxy(
                new AutoUpdateActionDelegate(), Action.AS_CHECK_BOX);
            action.setImageDescriptor(WorkbenchCorePlugin.getImageDescriptor("icons/autoupdate_e.png"));
            action.setDisabledImageDescriptor(WorkbenchCorePlugin.getImageDescriptor("icons/autoupdate_d.png"));
            action.setToolTipText("Automatically restarts processing after attributes change");
            return action; 
        }
    };
}
