
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

package org.carrot2.workbench.vis.aduna;

import org.carrot2.workbench.core.helpers.DropDownMenuAction;
import org.carrot2.workbench.core.ui.actions.IPropertyHost;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Event;

/**
 * Which clusters should be shown when the selection (or editor content) changes?
 */
public final class VisualizationModeAction extends Action
{
    /*
     * 
     */
    private final IMenuCreator menuCreator;

    /*
     * Common constructor.
     */
    public VisualizationModeAction(final String propertyKey, IPropertyHost host)
    {
        super("Update mode", Action.AS_DROP_DOWN_MENU);

        this.menuCreator = DropDownMenuAction.getMenuFor(propertyKey, getText(),
            VisualizationMode.class.getEnumConstants(), host);

        setImageDescriptor(AdunaActivator.imageDescriptorFromPlugin(
            AdunaActivator.PLUGIN_ID, "icons/selection.png"));
        setMenuCreator(menuCreator);
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
