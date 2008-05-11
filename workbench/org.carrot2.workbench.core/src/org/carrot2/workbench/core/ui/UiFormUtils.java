package org.carrot2.workbench.core.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 *
 */
public class UiFormUtils
{
    /**
     * Calls {@link FormToolkit#adapt(Control, boolean, boolean)} for given control. If
     * <code>control</code> is an instance of {@link Composite}, this method is called
     * recursively for all the children.
     */
    public static void adaptToFormUI(FormToolkit toolkit, Control control)
    {
        if (control instanceof Composite)
        {
            Composite c = (Composite) control;
            toolkit.adapt(c);
            for (int i = 0; i < c.getChildren().length; i++)
            {
                Control child = c.getChildren()[i];
                adaptToFormUI(toolkit, child);
            }
        }
        else
        {
            toolkit.adapt(control, true, true);
        }
    }
}
