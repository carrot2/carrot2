
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

package org.carrot2.workbench.core.ui.widgets;

import org.eclipse.swt.widgets.Composite;
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
}
