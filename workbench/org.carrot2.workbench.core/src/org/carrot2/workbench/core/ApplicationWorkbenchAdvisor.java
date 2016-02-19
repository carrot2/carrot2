
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

import org.carrot2.workbench.core.ui.perspectives.SearchPerspective;
import org.eclipse.ui.application.*;

/**
 * Advisor for initial settings and configuration.
 */
final class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor
{
    /*
     * 
     */
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
        IWorkbenchWindowConfigurer configurer)
    {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    /*
     * 
     */
    public String getInitialWindowPerspectiveId()
    {
        return SearchPerspective.ID;
    }

    /*
     * Main preference page.
     */
    public String getMainPreferencePageId()
    {
        return "org.carrot2.workbench.core.preferences.WorkbenchPreferencePage";
    }
    
    /*
     * 
     */
    @Override
    public void initialize(IWorkbenchConfigurer configurer)
    {
        super.initialize(configurer);
        configurer.setSaveAndRestore(true);
    }
}
