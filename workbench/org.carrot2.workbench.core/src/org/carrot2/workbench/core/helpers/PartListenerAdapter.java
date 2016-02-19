
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

package org.carrot2.workbench.core.helpers;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Empty implementation of {@link IPartListener}.
 */
public class PartListenerAdapter implements IPartListener
{
    public void partActivated(IWorkbenchPart part)
    {
    }

    public void partBroughtToTop(IWorkbenchPart part)
    {
    }

    public void partClosed(IWorkbenchPart part)
    {
    }

    public void partDeactivated(IWorkbenchPart part)
    {
    }

    public void partOpened(IWorkbenchPart part)
    {
    }
}
