package org.carrot2.workbench.core.ui;

import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.workbench.editors.AttributeEvent;
import org.carrot2.workbench.editors.IAttributeListener;
import org.eclipse.jface.fieldassist.ControlDecoration;

final class InvalidStateDecorationListener implements IAttributeListener
{
    private final ControlDecoration decoration;
    private AttributeDescriptor descriptor;

    private boolean showOverlay;
    private boolean valid;

    public InvalidStateDecorationListener(ControlDecoration d,
        AttributeDescriptor descriptor)
    {
        this.decoration = d;
        this.descriptor = descriptor;
    }

    public void valueChanged(AttributeEvent event)
    {
        if (event.key.equals(SearchInputView.ENABLE_VALIDATION_OVERLAYS))
        {
            this.showOverlay = true;
            updateOverlay();
        }

        if (event.key.equals(descriptor.key))
        {
            if (descriptor.isValid(event.value))
            {
                valid = true;
            }
            else
            {
                valid = false;
            }

            updateOverlay();
        }
    }

    private void updateOverlay()
    {
        if (valid || !showOverlay)
        {
            decoration.hide();
        }
        else
        {
            decoration.show();
        }
    }

    public void valueChanging(AttributeEvent event)
    {
        valueChanged(event);
    }
}
