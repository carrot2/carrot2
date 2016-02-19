
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

package org.carrot2.workbench.core;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * Entry point for the Workbench application.
 */
public final class Application implements IApplication
{
    private Display display;

    /**
     * 
     */
    public Object start(IApplicationContext context) throws Exception
    {
        display = PlatformUI.createDisplay();

        final int returnCode = PlatformUI.createAndRunWorkbench(display,
            new ApplicationWorkbenchAdvisor());

        display.dispose();

        if (returnCode == PlatformUI.RETURN_RESTART)
        {
            return IApplication.EXIT_RESTART;
        }
        else
        {
            return IApplication.EXIT_OK;
        }
    }

    /**
     * 
     */
    public void stop()
    {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null) return;

        final Display display = workbench.getDisplay();
        display.syncExec(new Runnable()
        {
            public void run()
            {
                if (!display.isDisposed()) workbench.close();
            }
        });
    }
}
