
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

package org.carrot2.workbench.core.ui;

import org.carrot2.workbench.core.helpers.DisposeBin;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * Produce a {@link MenuManager}. 
 */
abstract class MenuManagerCreator implements IMenuCreator
{
    private DisposeBin bin = new DisposeBin();

    public Menu getMenu(Control parent)
    {
        final Menu m = createMenu().createContextMenu(parent);
        bin.add(m);
        return m;
    }

    public Menu getMenu(Menu parent)
    {
        final Menu m = createMenu().getMenu();
        bin.add(m);
        return m;
    }
    
    public void dispose()
    {
        bin.dispose();
    }
    
    protected abstract MenuManager createMenu();
}
