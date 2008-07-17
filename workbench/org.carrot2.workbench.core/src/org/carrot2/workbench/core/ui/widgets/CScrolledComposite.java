package org.carrot2.workbench.core.ui.widgets;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;

/**
 * Custom extension of {@link SharedScrolledComposite} that adds
 * proper behavior for horizontal scrollbar. 
 */
public final class CScrolledComposite extends SharedScrolledComposite
{
    public CScrolledComposite(Composite parent, int style)
    {
        super(parent, style);
        this.setDelayedReflow(false);
    }

    /**
     * On reflow, update both vertical and horizontal scroller (for some reason the
     * horizontal one is neglected in current version of Eclipse).
     */
    @Override
    public void reflow(boolean flushCache)
    {
        super.reflow(flushCache);

        final ScrollBar hbar = getHorizontalBar();
        if (hbar != null)
        {
            final Rectangle clientArea = getClientArea();
            final int increment = Math.max(clientArea.width - 5, 5);
            hbar.setPageIncrement(increment);
        }
    }    
}
