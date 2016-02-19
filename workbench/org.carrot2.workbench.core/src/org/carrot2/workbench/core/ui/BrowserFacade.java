
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

import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

public class BrowserFacade
{
    public static Browser createNew(Composite parent, int flags)
    {
        try {
            return new Browser(parent, flags);
        } catch (SWTError e) {
            RuntimeException r = new RuntimeException("Could not instantiate Browser component.\n" +
                "See Carrot2 FAQ: http://project.carrot2.org/faq.html#browser", e);
            Utils.logError(r.getMessage(), e, true);
            throw e;
        }
    }
}
