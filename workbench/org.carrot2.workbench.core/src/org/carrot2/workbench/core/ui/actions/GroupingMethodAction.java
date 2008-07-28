package org.carrot2.workbench.core.ui.actions;

import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.eclipse.jface.action.Action;

/**
 * 
 */
final class GroupingMethodAction extends Action 
{
    public GroupingMethodAction()
    {
        super("Attribute grouping", Action.AS_DROP_DOWN_MENU);

        setImageDescriptor(WorkbenchCorePlugin.getImageDescriptor("icons/grouping.gif"));
    }
    
    @Override
    public void run()
    {
        /*
         * TODO: This seems strange, but AS_DROP_DOWN_MENU actions do not show context
         * menu popup when clicked. Eclipse has similar behaviour, so I assume
         * it is "normal".
         */
    }
}
