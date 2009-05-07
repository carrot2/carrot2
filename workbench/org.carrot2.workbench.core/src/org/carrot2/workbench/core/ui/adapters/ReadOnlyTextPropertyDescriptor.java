
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui.adapters;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * A {@link TextPropertyDescriptor} that displays a read-only text box
 * with the string value of a given property. No editing is allowed.
 */
public class ReadOnlyTextPropertyDescriptor extends TextPropertyDescriptor
{
    public ReadOnlyTextPropertyDescriptor(Object id, String displayName)
    {
        super(id, displayName);
    }

    public CellEditor createPropertyEditor(Composite parent)
    {
        TextCellEditor editor = new TextCellEditor(parent) {
            @Override
            protected void doSetValue(Object value)
            {
                super.doSetValue(value.toString());
            }

            @Override
            protected Control createControl(Composite parent)
            {
                Text txt = (Text) super.createControl(parent);
                text.setEditable(false);
                return txt;
            }
        };
        editor.setStyle(editor.getStyle() | SWT.READ_ONLY);
        return editor;
    }
}
