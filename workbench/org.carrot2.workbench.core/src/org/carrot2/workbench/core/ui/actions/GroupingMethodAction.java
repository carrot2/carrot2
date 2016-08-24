
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

import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.DropDownMenuAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchPart3;

/**
 * An action that displays a menu of possible {@link GroupingMethod}s.
 */
public final class GroupingMethodAction extends Action
{
    /*
     * 
     */
    private final IMenuCreator menuCreator;

    /*
     * Common constructor.
     */
    private GroupingMethodAction(final String propertyKey, IPropertyHost host)
    {
        super("Attribute grouping", Action.AS_DROP_DOWN_MENU);

        GroupingMethod [] constants = new GroupingMethod []
        {
            GroupingMethod.GROUP, GroupingMethod.LEVEL, GroupingMethod.STRUCTURE, null,
            GroupingMethod.NONE
        };
        this.menuCreator = DropDownMenuAction.getMenuFor(propertyKey, getText(),
            constants, host);

        setImageDescriptor(WorkbenchCorePlugin.getImageDescriptor("icons/grouping.png"));
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
        this(preferenceKey, new PreferenceStorePropertyHost(WorkbenchCorePlugin
            .getDefault().getPreferenceStore()));
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
