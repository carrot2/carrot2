package org.carrot2.workbench.core.ui.actions;

import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.DropDownMenuAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Event;

/**
 * An action that displays a menu of possible {@link GroupingMethod}.
 */
final class GroupingMethodAction extends Action 
{
    public GroupingMethodAction()
    {
        super("Attribute grouping", Action.AS_DROP_DOWN_MENU);

        setImageDescriptor(WorkbenchCorePlugin.getImageDescriptor("icons/grouping.gif"));
    }

    @Override
    public void runWithEvent(Event event)
    {
        /*
         * Attempt to open the drop-down menu.
         */
        DropDownMenuAction.showMenu(this, event);
    }
    
    @Override
    public IMenuCreator getMenuCreator()
    {
        return super.getMenuCreator();
    }
}
