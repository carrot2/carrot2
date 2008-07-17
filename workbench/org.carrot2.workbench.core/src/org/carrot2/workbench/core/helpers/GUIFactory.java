package org.carrot2.workbench.core.helpers;

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
     * Create a composite with {@link GridLayout} inside set to default margins.
     */
    public static Composite createSpacer(Composite parent)
    {
        final Composite spacer = new Composite(parent, SWT.NONE);
        spacer.setLayout(GridLayoutFactory.fillDefaults().margins(3, 3).create());
        return spacer;
    }
}
