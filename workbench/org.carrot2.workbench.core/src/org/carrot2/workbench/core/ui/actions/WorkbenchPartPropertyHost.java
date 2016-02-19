
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

package org.carrot2.workbench.core.ui.actions;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.IWorkbenchPart3;

/**
 * {@link IPropertyHost} delegating to {@link IWorkbenchPart3}.
 */
public final class WorkbenchPartPropertyHost implements IPropertyHost
{
    private final IWorkbenchPart3 part;

    public WorkbenchPartPropertyHost(IWorkbenchPart3 part)
    {
        this.part = part;
    }

    public void addPropertyChangeListener(IPropertyChangeListener listener)
    {
        this.part.addPartPropertyListener(listener);
    }

    public String getProperty(String key)
    {
        return part.getPartProperty(key);
    }

    public void setProperty(String key, String value)
    {
        part.setPartProperty(key, value);
    }

    public void removePropertyChangeListener(IPropertyChangeListener listener)
    {
        part.removePartPropertyListener(listener);
    }
}
