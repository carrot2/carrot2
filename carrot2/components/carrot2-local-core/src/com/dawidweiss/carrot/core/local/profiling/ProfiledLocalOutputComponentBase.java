
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.dawidweiss.carrot.core.local.profiling;

import com.dawidweiss.carrot.core.local.*;

/**
 * An implementation of the
 * {@link com.dawidweiss.carrot.core.local.LocalOutputComponent}that provides a
 * number of profiling utility methods. <b>Important </b>: profiling is
 * available <b>only </b> when the component is invoked with an instance of
 * {@link com.dawidweiss.carrot.core.local.profiling.ProfiledRequestContext},
 * which can be done by the
 * {@link com.dawidweiss.carrot.core.local.profiling.ProfiledLocalController}.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public abstract class ProfiledLocalOutputComponentBase extends
    LocalOutputComponentBase
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
     */
    protected void stopTimer()
    {
        if (profile != null)
        {
            profile.stopTimer();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#startProcessing(com.dawidweiss.carrot.core.local.RequestContext)
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
     * @see com.dawidweiss.carrot.core.local.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        profile = null;
    }

}