package org.carrot2.workbench.core;

import junit.framework.TestSuite;

import org.carrot2.workbench.editors.factory.TypeEditorWrapperTest;

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
    }

}
