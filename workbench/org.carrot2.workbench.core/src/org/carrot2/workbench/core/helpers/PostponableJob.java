
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

package org.carrot2.workbench.core.helpers;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Superclass of jobs that can be dynamically <i>postponed</i> (their execution schedule
 * can be dynamically changed).
 * <p>
 * This class is not a subclass of {@link Job} because scheduling methods in {@link Job}
 * are final and this could be confusing. Instead, pass the job to be executed
 * after a certain delay.
 */
public class PostponableJob
{
    /** Internal synchronization lock. */
    private final Object jobLock = new Object();

    /** Postponable delay job. */
    private Job job;
    
    /** Actual job to execute. */
    private final AtomicReference<Job> jobRef = new AtomicReference<>();

    public PostponableJob(Job actualJob)
    {
        setJob(actualJob);
    }
    
    public PostponableJob() {
	}

    protected final void setJob(Job actualJob)
    {
    	if (!this.jobRef.compareAndSet(null, actualJob))
    	{
    		throw new IllegalStateException("Can't set jobs twice."); 
    	}
	}

	/**
     * Schedule (or postpone) the job to execute after a given delay. Any previous
     * pending job is canceled.
     */
    public final void reschedule(int delay)
    {
    	final Job actualJob = jobRef.get();
    	if (actualJob == null) {
    		throw new IllegalStateException("Job not set.");
    	}

        final Job newJob = new Job(actualJob.getName() + " (Delayed)")
        {
            protected IStatus run(IProgressMonitor monitor)
            {
                synchronized (jobLock)
                {
                    if (job == this)
                    {
                        actualJob.schedule();
                        return Status.OK_STATUS;
                    }
                    else
                    {
                        return Status.CANCEL_STATUS;
                    }
                }
            }
        };
        newJob.setPriority(Job.INTERACTIVE);

        // System jobs are not visible in the GUI. I leave it for now, it's quite
        // interesting to see auto update tasks in the jobs panel.
        //
        // newJob.setSystem(true);

        synchronized (jobLock)
        {
            /*
             * Cancel previous job, but start the new one regardless of the previous job's
             * running state.
             */
            if (job != null && job.getState() == Job.SLEEPING)
            {
                job.cancel();
            }

            job = newJob;
            job.schedule(delay);
        }
    }
}
