package org.carrot2.util.tests;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.lang.mutable.MutableInt;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import com.carrotsearch.randomizedtesting.RandomizedContext;
import com.carrotsearch.randomizedtesting.RandomizedRunner;

/**
 * A {@link RunListener} that reports failed tests to standard error, along with the seed
 * information and switches that may be helpful to reproduce the test case.
 */
public class TestInfoListener extends RunListener
{
    private enum Status
    {
        OK, IGNORED, ERROR, FAILED, IGNORED_A;
        
        public String toString() {
            if (this != IGNORED_A)
                return super.toString();
            else
                return "A/IGNORED";
        }
    }

    private PrintStream output;

    /** Test's execution time. */
    private long startTime;

    /** Test execution status. */
    private Status status;

    private MutableInt mutex;
    private PrintStream prevOut;
    private PrintStream prevErr;

    public TestInfoListener()
    {
        output = System.out;
    }

    @Override
    public void testRunStarted(Description description) throws Exception
    {
        output.println();
        output.println("Suite: " + description.getDisplayName());
    }

    @Override
    public void testStarted(Description description) throws Exception
    {
        String testName = description.getMethodName();
        if (testName.length() > 55)
        {
            testName = testName.substring(0, 55 - 3) + "...";
        }
        output.print(String.format(Locale.ENGLISH, "  %-55s", testName));
        output.flush();

        prevOut = System.out;
        prevErr = System.err;
        mutex = new MutableInt();
        System.setOut(new PrintStream(new PrefixedOutputStream("  1> ".getBytes(), mutex, output)));
        System.setErr(new PrintStream(new PrefixedOutputStream("  2> ".getBytes(), mutex, output)));

        status = Status.OK;
        startTime = System.currentTimeMillis();
    }

    @Override
    public void testFinished(Description description) throws Exception
    {
        System.out.flush();
        System.err.flush();
        System.setOut(prevOut);
        System.setErr(prevErr);

        if (mutex.intValue() > 0)
        {
            // Align if anything's been written to syserr/sysout.
            output.print(String.format(Locale.ENGLISH, "  %55s", "..."));
        }
        double time = (System.currentTimeMillis() - startTime) / 1000.0D;
        output.println(String.format(Locale.ENGLISH, " %10s [%5.2fs]", status, time));
        output.flush();        
    }

    @Override
    public void testAssumptionFailure(Failure failure)
    {
        status = Status.IGNORED_A;
    }

    @Override
    public void testIgnored(Description description) throws Exception
    {
        status = Status.IGNORED;
    }

    @Override
    public void testFailure(Failure failure) throws Exception
    {
        status = Status.ERROR;

        final Description d = failure.getDescription();
        final StringBuilder b = new StringBuilder();
        b.append("TEST FAILED : ").append(d.getDisplayName()).append("\n");
        b.append("Message  : " + failure.getMessage() + "\n");
        b.append("Reproduce:");
        b.append(" -D").append(RandomizedRunner.SYSPROP_RANDOM_SEED).append("=")
            .append(RandomizedContext.current().getRunnerSeed());
        if (d.getClassName() != null) b.append(" -D")
            .append(RandomizedRunner.SYSPROP_TESTCLASS).append("=")
            .append(d.getClassName());
        if (d.getMethodName() != null) b.append(" -D")
            .append(RandomizedRunner.SYSPROP_TESTMETHOD).append("=")
            .append(RandomizedRunner.stripSeed(d.getMethodName()));

        for (String p : Arrays.asList(RandomizedRunner.SYSPROP_ITERATIONS,
            RandomizedRunner.SYSPROP_NIGHTLY))
        {
            if (System.getProperty(p) != null)
            {
                b.append(" -D").append(p).append("=").append(System.getProperty(p));
            }
        }
        b.append("\n");
        b.append("Throwable: " + failure.getTrace());

        System.err.println(b.toString());
    }
}
