
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

package org.carrot2.util.tests;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import org.carrot2.shaded.guava.common.base.Strings;

/**
 * Writes plain-text information about the suite's tests and their results. For
 * aggregating into a summary later on.
 */
public class SuiteResultInfoWriter extends RunListener
{
    private enum Status
    {
        OK, IGNORED, ERROR, FAILED, ASSUMPTION_IGNORED, UNDEFINED;

        public String toString()
        {
            if (this != ASSUMPTION_IGNORED) return super.toString();
            else return "A/IGNORED";
        }
    }

    private PrintWriter writer;
    private HashMap<String, Status> tests = new HashMap<String, Status>();

    @Override
    public void testRunStarted(Description description) throws Exception
    {
        String verboseReportsDir = System.getProperty("verbose.reports.dir");
        if (!Strings.isNullOrEmpty(verboseReportsDir))
        {
            writer = new PrintWriter(
                new File(verboseReportsDir).getAbsolutePath() + "/" +
                    "RESULTS-" + description.getClassName() + ".txt");
        }
        tests.clear();
    }

    @Override
    public void testStarted(Description description) throws Exception
    {
        tests.put(description.getDisplayName(), Status.OK);
    }

    @Override
    public void testIgnored(Description description) throws Exception
    {
        tests.put(description.getDisplayName(), Status.IGNORED);
    }

    @Override
    public void testFailure(Failure failure) throws Exception
    {
        tests.put(failure.getDescription().getDisplayName(),
            failure.getException() instanceof AssertionError 
            ? Status.FAILED 
            : Status.ERROR);
    }

    @Override
    public void testAssumptionFailure(Failure failure)
    {
        tests.put(failure.getDescription().getDisplayName(), Status.ASSUMPTION_IGNORED);
    }

    @Override
    public void testRunFinished(Result result) throws Exception
    {
        if (writer != null)
        {
            try
            {
                for (Map.Entry<String, Status> e : tests.entrySet())
                {
                    writer.println(String.format(Locale.ENGLISH, "%-10s | %s",
                        e.getValue().toString(),
                        e.getKey()));
                }
            }
            finally
            {
                writer.close();
                writer = null;
            }
        }
    }
}
