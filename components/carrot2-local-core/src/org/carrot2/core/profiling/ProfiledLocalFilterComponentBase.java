
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

package org.carrot2.core.profiling;

import org.carrot2.core.*;

/**
 * An implementation of the
 * {@link org.carrot2.core.LocalFilterComponent}that provides a
 * number of profiling utility methods. <b>Important </b>: profiling is
 * available <b>only </b> when the component is invoked with an instance of
 * {@link org.carrot2.core.profiling.ProfiledRequestContext},
 * which can be done by the
 * {@link org.carrot2.core.profiling.ProfiledLocalController}.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ProfiledLocalFilterComponentBase extends LocalFilterComponentBase
{
    /** Profile of execution for this query */
    protected Profile profile;

    /**
     * Starts measuring time spent by the component on processing the query.
     * Make sure you stop the timer before calling methods on components next in
     * the processing chain. If you don't you're likely to measure the
     * processing time of the whole chain.
     */
    protected void startTimer()
    {
        if (profile != null)
        {
            profile.startTimer();
        }
    }

    /**
     * Stops measuring time spent by the component on processing the query.
     * 
     * @return time elapsed since the <b>last </b> call to {@link #startTimer()}.
     */
    protected long stopTimer()
    {
        if (profile != null)
        {
            return profile.stopTimer();
        }
        else
        {
            return -1;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#startProcessing(org.carrot2.core.RequestContext)
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        super.startProcessing(requestContext);
        if (requestContext instanceof ProfiledRequestContext)
        {
            profile = ((ProfiledRequestContext) requestContext)
                .getProfile(this);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        profile = null;
    }
}