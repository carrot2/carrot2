
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

package org.carrot2.core.profiling;

import java.util.Map;

import org.carrot2.core.*;

/**
 * An implementation of the
 * {@link org.carrot2.core.LocalController}interface that
 * provides components with an instance of the
 * {@link org.carrot2.core.profiling.ProfiledRequestContext},
 * thus enabling them to collect profiling and debugging information during
 * query processing. The
 * {@link org.carrot2.core.profiling.ProfiledRequestContext}can
 * be obtained from the
 * {@link org.carrot2.core.ProcessingResult}object by narrowing
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
     * @see org.carrot2.core.LocalController#query(java.lang.String,
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