package org.carrot2.workbench.core;

import junit.framework.TestSuite;

public class AllTests extends TestSuite
{
    public static TestSuite suite()
    {
        return new AllTests();
    }

    public AllTests()
    {
        this.addTestSuite(ProcessingJobTest.class);
        // this.addTestSuite(TypeEditorWrapperTest.class);
    }

}
