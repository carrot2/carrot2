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
        this.addTestSuite(MiscellaneousTests.class);
        this.addTestSuite(TypeEditorWrapperTest.class);
        this.addTestSuite(DedicatedEditorWrapperTest.class);
        this.addTestSuite(FactoryTest.class);
    }

}
