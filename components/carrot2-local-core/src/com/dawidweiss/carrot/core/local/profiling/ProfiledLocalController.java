/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.core.local.profiling;

import java.util.*;

import com.dawidweiss.carrot.core.local.*;

/**
 * An implementation of the
 * {@link com.dawidweiss.carrot.core.local.LocalController}interface that
 * provides components with an instance of the
 * {@link com.dawidweiss.carrot.core.local.profiling.ProfiledRequestContext},
 * thus enabling them to collect profiling and debugging information during
 * query processing. The
 * {@link com.dawidweiss.carrot.core.local.profiling.ProfiledRequestContext}can
 * be obtained from the
 * {@link com.dawidweiss.carrot.core.local.ProcessingResult}object by narrowing
 * its {@link RequestContext}.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ProfiledLocalController extends LocalControllerBase
{
    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalController#query(java.lang.String,
     *      java.lang.String, java.util.Map)
     */
    public ProcessingResult query(String processId, String query,
        Map requestParameters) throws MissingProcessException, Exception
    {
        // Get the process
        LocalProcess process = (LocalProcess) processes.get(processId);
        if (process == null)
        {
            throw new MissingProcessException("No such process: " + processId);
        }

        ProfiledRequestContext requestContext = new ProfiledRequestContext(
            this, requestParameters);

        try
        {
            Object result = process.query(requestContext, query);

            return new Result(result, requestContext);
        }
        finally
        {
            requestContext.dispose();
        }
    }
}