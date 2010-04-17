
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

import org.carrot2.workbench.core.helpers.SimpleXmlMementoTest;
import org.carrot2.workbench.core.ui.SaveOptionsSerializationTest;
import org.carrot2.workbench.editors.factory.*;
import org.eclipse.ui.IWorkbench;

/**
 * Headless tests (no {@link IWorkbench} available.
 */
public class CoreTests extends TestSuite
{
    public static TestSuite suite()
    {
        return new CoreTests();
    }

    public CoreTests()
    {
        this.addTestSuite(TypeEditorWrapperTest.class);
        this.addTestSuite(DedicatedEditorWrapperTest.class);
        this.addTestSuite(FactoryTest.class);
        this.addTestSuite(SimpleXmlMementoTest.class);
        this.addTestSuite(SaveOptionsSerializationTest.class);
    }
}
