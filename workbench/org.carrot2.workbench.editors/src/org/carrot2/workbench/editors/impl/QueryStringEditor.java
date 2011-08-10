package org.carrot2.workbench.editors.impl;

import org.carrot2.workbench.core.helpers.GUIFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


public class QueryStringEditor extends StringEditor
{
    @Override
    protected Text createTextBox(Composite parent, int gridColumns)
    {
        Text textBox = new Text(parent, SWT.SEARCH | SWT.ICON_SEARCH | SWT.BORDER );
        textBox.setLayoutData(
            GUIFactory.editorGridData()
                .grab(true, false)
                .hint(200, SWT.DEFAULT)
                .align(SWT.FILL, SWT.CENTER)
                .span(gridColumns, 1).create());
        return textBox;
    }
}
