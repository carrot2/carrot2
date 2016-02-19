
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

package org.carrot2.workbench.editors;

import org.eclipse.swt.layout.GridLayout;

/**
 * Hints about visual layout of an {@link IAttributeEditor}.
 */
public final class AttributeEditorInfo
{
    /**
     * Number of columns in the {@link GridLayout} this editor usually occupies. The
     * number of actual columns passed to
     * {@link IAttributeEditor#createEditor(org.eclipse.swt.widgets.Composite, int)} may
     * be larger, but never smaller than this value.
     */
    public final int columns;

    /**
     * If <code>true</code>, then the editor displays its own label. Otherwise the
     * containing component must display the edited attribute's label on its own.
     */
    public final boolean displaysOwnLabel;

    /*
     * 
     */
    public AttributeEditorInfo(int columns, boolean displaysOwnLabel)
    {
        this.columns = columns;
        this.displaysOwnLabel = displaysOwnLabel;
    }
}
