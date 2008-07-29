package org.carrot2.workbench.core.ui.actions;

import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.DisposeBin;
import org.carrot2.workbench.core.helpers.DropDownMenuAction;
import org.eclipse.jface.action.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchPart3;

/**
 * An action that displays a menu of possible {@link GroupingMethod}s.
 */
public final class GroupingMethodAction extends Action
{
    /*
     * 
     */
    private class ToggleSwitchAction extends ValueSwitchAction
    {
        public ToggleSwitchAction(GroupingMethod method, String label)
        {
            super(propertyKey, method.name(), label, Action.AS_RADIO_BUTTON, host);
        }
    }
    
    /*
     * 
     */
    private final String propertyKey;

    /*
     * 
     */
    private final IPropertyHost host;

    /*
     * 
     */
    private final IMenuCreator menuCreator = new IMenuCreator()
    {
        private DisposeBin bin = new DisposeBin();

        public Menu getMenu(Control parent)
        {
            Menu m = createMenu(propertyKey).createContextMenu(parent);
            bin.add(m);
            return m;
        }

        public Menu getMenu(Menu parent)
        {
            Menu m = createMenu(propertyKey).getMenu();
            bin.add(m);
            return createMenu(propertyKey).getMenu();
        }

        public void dispose()
        {
            bin.dispose();
        }
        
        /**
         * Creates a {@link IMenuManager} with {@link GroupingMethod} options, associated to a
         * given key in the preference store.
         */
        private MenuManager createMenu(String preferenceKey)
        {
            final MenuManager menu = new MenuManager("Attribute &grouping");

            final ToggleSwitchAction [] layoutActions = new ToggleSwitchAction []
            {
                new ToggleSwitchAction(GroupingMethod.GROUP, "Attribute semantics"),
                new ToggleSwitchAction(GroupingMethod.LEVEL, "Attribute level"),
                new ToggleSwitchAction(GroupingMethod.STRUCTURE, "Declaring class"), 
                null, /* Separator */
                new ToggleSwitchAction(GroupingMethod.NONE, "None"),
            };

            for (ToggleSwitchAction action : layoutActions)
            {
                if (action == null)
                {
                    menu.add(new Separator());
                    continue;
                }

                menu.add(action);
                bin.add(action);
            }

            return menu;
        }
    };

    /*
     * Common constructor.
     */
    private GroupingMethodAction(final String propertyKey, IPropertyHost host)
    {
        super("Attribute grouping", Action.AS_DROP_DOWN_MENU);

        this.propertyKey = propertyKey;
        this.host = host;

        setImageDescriptor(WorkbenchCorePlugin.getImageDescriptor("icons/grouping.gif"));
        setMenuCreator(menuCreator);
    }

    /**
     * Creates a grouping action bound to a part's property.
     */
    public GroupingMethodAction(final String partPreferenceKey, IWorkbenchPart3 part)
    {
        this(partPreferenceKey, new WorkbenchPartPropertyHost(part));
    }

    /**
     * Creates a grouping action bound to the global plugin's preference store key.
     */
    public GroupingMethodAction(final String preferenceKey)
    {
        this(preferenceKey, new PreferenceStorePropertyHost(
            WorkbenchCorePlugin.getDefault().getPreferenceStore()));
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
}
