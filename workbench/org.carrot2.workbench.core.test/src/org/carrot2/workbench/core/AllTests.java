
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core;

import junit.framework.TestSuite;

import org.carrot2.workbench.editors.factory.*;

public class AllTests extends TestSuite
{
    public static TestSuite suite()
    {
        return new AllTests();
    }

    public AllTests()
    {
        this.addTestSuite(ProcessingJobTest.class);
        this.addTestSuite(TypeEditorWrapperTest.class);
        this.addTestSuite(DedicatedEditorWrapperTest.class);
        this.addTestSuite(FactoryTest.class);
    }

}
