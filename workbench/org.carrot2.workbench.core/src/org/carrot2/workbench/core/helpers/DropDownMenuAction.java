
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

package org.carrot2.workbench.core.helpers;

import org.carrot2.workbench.core.ui.actions.IPropertyHost;
import org.carrot2.workbench.core.ui.actions.ValueSwitchAction;
import org.eclipse.jface.action.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;

/**
 * Delegation functionality for {@link IAction#runWithEvent(Event)}, methods of
 * {@link IAction#AS_DROP_DOWN_MENU} actions that wish to open a popup-menu.
 */
public final class DropDownMenuAction
{
    private DropDownMenuAction()
    {
    }

    /**
     * Show a menu for the given action.
     */
    public static void showMenu(IAction action, Event e)
    {
        final IMenuCreator mc = action.getMenuCreator();

        if (mc != null && e != null)
        {
            final ToolItem ti = (ToolItem) e.widget;
            if (ti != null && ti instanceof ToolItem)
            {
                final Menu m = mc.getMenu((ti).getParent());
                if (m != null)
                {
                    final Rectangle b = ti.getBounds();
                    final Point p = ti.getParent().toDisplay(
                        new Point(b.x, b.y + b.height));
                    m.setLocation(p.x, p.y);
                    m.setVisible(true);
                }
            }
        }
    }

    /**
     * Create a {@link IMenuCreator} that shows actions related to a given property, with
     * names and values derived from an enum constant.
     */
    public static <E extends Enum<E>> IMenuCreator getMenuFor(final String propertyKey,
        final String menuTitle, final E [] constants, final IPropertyHost host)
    {
        return new IMenuCreator()
        {
            private DisposeBin bin = new DisposeBin();
    
            public Menu getMenu(Control parent)
            {
                final Menu m = createMenu(propertyKey).createContextMenu(parent);
                bin.add(m);
                return m;
            }
    
            public Menu getMenu(Menu parent)
            {
                final Menu m = createMenu(propertyKey).getMenu();
                bin.add(m);
                return createMenu(propertyKey).getMenu();
            }
    
            public void dispose()
            {
                bin.dispose();
            }
    
            private MenuManager createMenu(String preferenceKey)
            {
                final MenuManager menu = new MenuManager(menuTitle);
    
                for (E e : constants)
                {
                    if (e == null)
                    {
                        menu.add(new Separator());
                        continue;
                    }
    
                    final ValueSwitchAction action = new ValueSwitchAction(propertyKey, e
                        .name(), e.toString(), Action.AS_RADIO_BUTTON, host);
    
                    menu.add(action);
                    bin.add(action);
                }
    
                return menu;
            }
        };
    }
}
