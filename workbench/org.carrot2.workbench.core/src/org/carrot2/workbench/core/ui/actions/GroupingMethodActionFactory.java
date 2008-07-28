package org.carrot2.workbench.core.ui.actions;

import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.eclipse.jface.action.*;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * @see #createMenu(String)
 */
public final class GroupingMethodActionFactory
{
    private static class ToggleSwitchAction extends ValueSwitchAction
    {
        public ToggleSwitchAction(String key, GroupingMethod method, String label)
        {
            super(key, method.name(), label, Action.AS_RADIO_BUTTON);
        }
    }

    private GroupingMethodActionFactory()
    {
        // no instances.
    }

    /**
     * Creates a {@link IMenuManager} with {@link GroupingMethod} options, associated
     * to a given key in the preference store. 
     */
    public static MenuManager createMenu(String preferenceKey)
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

    /**
     * Creates an expandable (to menu) action with {@link GroupingMethod} options, associated
     * to a given key in the preference store. 
     */
    public static IAction createAction(final String preferenceKey)
    {
        final GroupingMethodAction action = new GroupingMethodAction();

        action.setMenuCreator(new IMenuCreator() {
            public Menu getMenu(Control parent)
            {
                return createMenu(preferenceKey).createContextMenu(parent);
            }

            public Menu getMenu(Menu parent)
            {
                return createMenu(preferenceKey).getMenu();
            }
            
            public void dispose()
            {
            }
        });

        return action;
    }
}
