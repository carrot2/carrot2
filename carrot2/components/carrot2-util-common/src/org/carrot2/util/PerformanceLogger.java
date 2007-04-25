/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * A thread-safe, nested performance logger.
 */
public final class PerformanceLogger
{
    /** The actual Log4j sink */
    private final Logger sink;

    /** Level at which to log. */
    private Level level;

    /**
     * A bunch of information about the most recent job, stored in the thread's local
     * data.
     */
    private static class JobInfo
    {
        public final long start;
        public final String jobName;
        public final String prefix;
        public final String full;

        JobInfo(String jobName, String prefix, String full)
        {
            this.jobName = jobName;
            this.prefix = prefix;
            this.full = full;
            this.start = System.currentTimeMillis();
        }

        public long getDuration()
        {
            return System.currentTimeMillis() - start;
        }
    }

    /**
     * A stack of {@link JobInfo} nested objects. The most recent job is last on the list.
     * <code>ThreadLocal<ArrayList<JobInfo>></code>
     */
    private final ThreadLocal jobs = new ThreadLocal()
    {
        protected Object initialValue()
        {
            return new ArrayList(5);
        }
    };

    /**
     * Precompiled {@link MessageFormat} object (message formats are not thread safe and
     * their compilation is lengthy, so this is a tradeoff).
     */
    private final ThreadLocal mformat = new ThreadLocal()
    {
        protected Object initialValue()
        {
            return new MessageFormat("{0,number,0.00} sec.", Locale.ENGLISH);
        }
    };

    /**
     * 
     */
    public PerformanceLogger(Level level, Logger sink)
    {
        this.sink = sink;
        this.level = level;
    }

    /**
     * Logs information about the job being started. Records the current time. <b>This
     * method must be accompanied by a call to {@link #end()}.</b>
     */
    public void start(String jobName)
    {
        if (!sink.isEnabledFor(level))
        {
            return;
        }

        final ArrayList stack = (ArrayList) jobs.get();
        final String prefix;
        final String full;

        if (stack.size() == 0)
        {
            prefix = "";
            full = jobName;
        }
        else
        {
            final JobInfo prev = (JobInfo) stack.get(stack.size() - 1);
            prefix = prev.prefix + prev.jobName + " > ";
            full = prefix + jobName;
        }

        stack.add(new JobInfo(jobName, prefix, full));

        sink.log(level, "START: " + full);
    }

    /**
     * Logs information about the finished job: duration, nesting and job name.
     */
    public final void end()
    {
        if (!sink.isEnabledFor(level))
        {
            return;
        }

        end(this.level, null);
    }

    /**
     * Reset any nesting (prevents indefinite growing of the internal stack).
     */
    public final void reset()
    {
        final ArrayList stack = (ArrayList) jobs.get();
        while (!stack.isEmpty())
        {
            end();
        }
    }

    /**
     * Logs information about the finished job: duration and an optional message.
     */
    public void end(String message)
    {
        end(this.level, message);
    }

    /**
     * Logs information about the finished job: duration, job name, optional message.
     */
    public void end(Level level, String message)
    {
        if (!sink.isEnabledFor(level))
        {
            return;
        }

        final ArrayList stack = (ArrayList) jobs.get();
        final JobInfo job = (JobInfo) stack.remove(stack.size() - 1);
        final MessageFormat mf = (MessageFormat) mformat.get();

        sink.log(level, "END: " + job.full + " (" + mf.format(new Object []
        {
            new Double(job.getDuration() / 1000.0)
        }) + ") " + (message == null ? "" : (" {" + message + "}")));
    }
}