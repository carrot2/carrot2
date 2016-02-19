
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

package org.carrot2.workbench.core.preferences;

import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * General preferences (category).
 */
public class GeneralPreferencesPage extends PreferencePage implements
    IWorkbenchPreferencePage
{
    protected Control createContents(Composite parent)
    {
        return new Composite(parent, SWT.NULL);
    }

    /**
     * Hook method to get a page specific preference store. Reimplement this method if a
     * page don't want to use its parent's preference store.
     */
    protected IPreferenceStore doGetPreferenceStore()
    {
        return WorkbenchCorePlugin.getDefault().getPreferenceStore();
    }

    /**
     * @see IWorkbenchPreferencePage
     */
    public void init(IWorkbench workbench)
    {
    }
}
