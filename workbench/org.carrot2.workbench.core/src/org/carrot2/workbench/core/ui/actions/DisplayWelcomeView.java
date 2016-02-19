
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

import java.net.URL;

import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * Displays Carrot2 (custom) welcome view.
 */
public final class DisplayWelcomeView extends AbstractHandler
{
    /**
     * Prefix for properties, preference keys, etc.
     */
    private static final String PREFIX = DisplayWelcomeView.class.getName();

    /**
     * Bundle resource with the welcome view's index page.
     */
    private static final String WELCOME_INDEX_PATH = "/welcome/index.html";

    /**
     * Welcome view browser identifier.
     */
    private static final String BROWSER_ID = DisplayWelcomeView.class.getName() + ".browser";

    /**
     * Preference key for marking initial view display.
     */
    public static final String ALREADY_DISPLAYED = PREFIX + ".displayed";

    /**
     * Displays the welcome view.
     */
    public void execute()
    {
        execute(null);
    }

    /**
     * Displays the welcome view ({@link IHandler} callback).
     */
    @Override
    public Object execute(ExecutionEvent event)
    {
        try
        {
            final WorkbenchCorePlugin wbCore = WorkbenchCorePlugin.getDefault();
            URL index = FileLocator.find(wbCore.getBundle(), 
                new Path(WELCOME_INDEX_PATH), null);

            if (index != null)
            {
                index = FileLocator.toFileURL(index);

                IWebBrowser browser = wbCore.getWorkbench().getBrowserSupport()
                    .createBrowser(
                        IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.NAVIGATION_BAR,
                        BROWSER_ID, null, null);
                
                browser.openURL(index);
            }
        }
        catch (Exception e)
        {
            Utils.logError("Failed to open the welcome view.", e, false);
        }
        finally
        {
            WorkbenchCorePlugin.getPreferences().putBoolean(
                ALREADY_DISPLAYED, true);
        }

        return null;
    }
}
