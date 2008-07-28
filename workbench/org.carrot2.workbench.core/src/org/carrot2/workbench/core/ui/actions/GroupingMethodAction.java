package org.carrot2.workbench.core.ui.actions;

import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.DisposeBin;
import org.carrot2.workbench.core.helpers.DropDownMenuAction;
import org.eclipse.jface.action.*;
import org.eclipse.swt.widgets.*;

/**
 * An action that displays a menu of possible {@link GroupingMethod}.
 */
public final class GroupingMethodAction extends Action 
{
    /*
     * 
     */
    private static class ToggleSwitchAction extends ValueSwitchAction
    {
        public ToggleSwitchAction(String key, GroupingMethod method, String label)
        {
            super(key, method.name(), label, Action.AS_RADIO_BUTTON);
        }
    }

    /*
     * 
     */
    public GroupingMethodAction(final String preferenceKey)
    {
        super("Attribute grouping", Action.AS_DROP_DOWN_MENU);

        setImageDescriptor(WorkbenchCorePlugin.getImageDescriptor("icons/grouping.gif"));

        setMenuCreator(new IMenuCreator()
        {
            private DisposeBin bin = new DisposeBin(); 

            public Menu getMenu(Control parent)
            {
                Menu m = createMenu(preferenceKey).createContextMenu(parent);
                bin.add(m);
                return m;
            }

            public Menu getMenu(Menu parent)
            {
                Menu m = createMenu(preferenceKey).getMenu();
                bin.add(m);
                return createMenu(preferenceKey).getMenu();
            }

            public void dispose()
            {
                bin.dispose();
            }
        });
    }

    /*
     * 
     */
    @Override
    public void runWithEvent(Event event)
    {
        /*
         * Attempt to open the drop-down menu.
         */
        DropDownMenuAction.showMenu(this, event);
    }
    
    /**
     * Creates a {@link IMenuManager} with {@link GroupingMethod} options, associated
     * to a given key in the preference store. 
     */
    private static MenuManager createMenu(String preferenceKey)
    {
        final MenuManager menu = new MenuManager("Attribute &grouping");

        final ToggleSwitchAction [] layoutActions = new ToggleSwitchAction []
        {
            new ToggleSwitchAction(preferenceKey, GroupingMethod.GROUP, "Attribute semantics"),
            new ToggleSwitchAction(preferenceKey, GroupingMethod.LEVEL, "Attribute level"),
            new ToggleSwitchAction(preferenceKey, GroupingMethod.STRUCTURE, "Declaring class"), 
            null, /* Separator */
            new ToggleSwitchAction(preferenceKey, GroupingMethod.NONE, "None"),
        };

        for (IAction action : layoutActions)
        {
            if (action == null)
            {
                menu.add(new Separator());
                continue;
            }

            menu.add(action);
        }

        return menu;
    }
}
