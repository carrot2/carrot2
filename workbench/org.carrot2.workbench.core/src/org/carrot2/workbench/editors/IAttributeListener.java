
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

import java.util.EventListener;

/**
 * Listener receiving attribute modification events.
 */
public interface IAttributeListener extends EventListener
{
    /**
     * Invoked when the attribute's value has changed.
     */
    public void valueChanged(AttributeEvent event);

    /**
     * Invoked when the attribute's value is changing, but these changes have not been
     * committed yet.
     * <p>
     * Not all editors must send this kind of event. Typically, editors with text boxes
     * will emit on-keystroke content changes.
     */
    public void valueChanging(AttributeEvent event);
}
