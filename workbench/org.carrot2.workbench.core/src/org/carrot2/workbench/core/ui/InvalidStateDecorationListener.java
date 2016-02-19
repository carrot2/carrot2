
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

package org.carrot2.workbench.core.ui;

import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.editors.AttributeEvent;
import org.carrot2.workbench.editors.IAttributeListener;
import org.eclipse.jface.fieldassist.ControlDecoration;

final class InvalidStateDecorationListener implements IAttributeListener
{
    private final ControlDecoration decoration;
    private AttributeDescriptor descriptor;

    private boolean showOverlay;
    private boolean valid;

    public InvalidStateDecorationListener(
        ControlDecoration d, AttributeDescriptor descriptor, Object defaultValue)
    {
        this.decoration = d;
        this.descriptor = descriptor;

        valueChanged(new AttributeEvent(this, descriptor.key, defaultValue));
    }

    public void valueChanged(AttributeEvent event)
    {
        if (event.key.equals(AttributeList.ENABLE_VALIDATION_OVERLAYS))
        {
            this.showOverlay = true;
            updateOverlay();
        }
        else if (event.key.equals(descriptor.key))
        {
            try {
                valid = descriptor.isValid(event.value);
            } catch (Exception e) {
                Utils.logError(e, false);
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
