
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core;

import junit.framework.TestSuite;

public class GuiTests extends TestSuite
{
    public static TestSuite suite()
    {
        return new GuiTests();
    }

    public GuiTests()
    {
        this.addTestSuite(ProcessingJobTest.class);
        this.addTestSuite(NativeLibrariesTest.class);
        this.addTestSuite(WorkbenchStartupTest.class);
    }
}
