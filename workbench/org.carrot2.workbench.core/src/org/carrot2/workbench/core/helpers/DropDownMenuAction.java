package org.carrot2.workbench.core.helpers;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
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
     * 
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
}
