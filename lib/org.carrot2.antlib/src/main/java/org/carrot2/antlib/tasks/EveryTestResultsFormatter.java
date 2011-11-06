package org.carrot2.antlib.tasks;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

import junit.framework.AssertionFailedError;
import junit.framework.Test;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitVersionHelper;

/**
 * A more verbose results formatter dumping each test case, its output streams and status.
 * Unfortunately ANT's JUnit API is crippled and doesn't pass any information about ignored/
 * assumption-ignored tests.  
 */
public class EveryTestResultsFormatter implements JUnitResultFormatter
{
    private enum Status {
        OK,
        IGNORED,
        ERROR,
        FAILED,
        ASSUMPTION
    }

    /** The output to save the results to. */
    private PrintStream output;
    
    /** Test's execution time. */
    private long startTime;
    
    /** Test execution status. */
    private Status status;

    @Override
    public void startTestSuite(JUnitTest suite) throws BuildException
    {
        output.println("Suite: " + suite.getName());
    }

    @Override
    public void startTest(Test test)
    {
        String testName = JUnitVersionHelper.getTestCaseName(test);
        if (testName.length() > 55) {
            testName = testName.substring(0, 55 - 3) + "...";
        }
        output.print(
            String.format(Locale.ENGLISH, "  %-55s", testName));
        output.flush();

        status = Status.OK;
        startTime = System.currentTimeMillis();
    }

    @Override
    public void addError(Test test, Throwable t)
    {
        status = Status.ERROR;
    }

    @Override
    public void addFailure(Test test, AssertionFailedError t)
    {
        status = Status.FAILED;
    }

    @Override
    public void endTest(Test test)
    {
        double time = (System.currentTimeMillis() - startTime) / 1000.0D;
        output.println(
            String.format(Locale.ENGLISH, " %10s [%5.2fs]",
                status, time));
    }

    @Override
    public void endTestSuite(JUnitTest suite) throws BuildException
    {
        output.println();
    }

    @Override
    public void setOutput(OutputStream output)
    {
        this.output = new PrintStream(output);
    }

    @Override
    public void setSystemError(String syserr)
    {
        // Ignore, we're collecting our own outputs?
    }

    @Override
    public void setSystemOutput(String sysoutput)
    {
        // Ignore, we're collecting our own outputs?
    }
}
