
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

package org.carrot2.workbench.editors.impl;

import static org.eclipse.swt.SWT.BORDER;
import static org.eclipse.swt.SWT.MULTI;

import org.carrot2.workbench.core.helpers.GUIFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Editor for multi-line {@link String}s.
 */
public class TextareaEditor extends StringEditor
{
    @Override
    protected Text createTextBox(Composite parent, int gridColumns)
    {
        Text textBox = new Text(parent, BORDER | MULTI);
        textBox.setLayoutData(GUIFactory.editorGridData().grab(true, false).hint(200,
            80).align(SWT.FILL, SWT.CENTER).span(gridColumns, 1).create());
        return textBox;
    }
}
