
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

import org.carrot2.workbench.editors.IAttributeEditor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public final class GUIFactory
{
    private GUIFactory()
    {
    }

    /**
     * Creates a {@link GridLayout} with zero margins.
     */
    public static GridLayout zeroMarginGridLayout()
    {
        final GridLayout gd = new GridLayout();
        gd.marginWidth = 0;
        gd.marginHeight = 0;
        gd.marginBottom = 0;
        gd.marginLeft = 0;
        gd.marginRight = 0;
        gd.marginTop = 0;
        return gd;
    }

    /**
     * Return a {@link GridDataFactory} suitable for default {@link IAttributeEditor}
     * cells.
     */
    public static GridDataFactory editorGridData()
    {
        return GridDataFactory.fillDefaults();
    }

    /**
     * Create a composite with {@link GridLayout} inside set to default margins.
     */
    public static Composite createSpacer(Composite parent)
    {
        final Composite spacer = new Composite(parent, SWT.NONE | SWT.WRAP);
        spacer.setLayout(GridLayoutFactory.fillDefaults().margins(5, 3).create());
        return spacer;
    }
}
