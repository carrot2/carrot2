
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.webapp;

import java.util.Map;

import javax.servlet.ServletConfig;

/**
 * A facility for performing query expansion.
 *
 * @author Stanislaw Osinski
 */
public interface QueryExpander
{
    /**
     * Configures this query expander.
     */
    public void configure(ServletConfig config);
    
    /**
     * Returns query after expansion. If the the query expansion mechanism is unable to
     * add anything to the original query, <code>null</code> should be returned.
     *
     * @param query original query to be expanded
     * @param requestParameters request parameters that may contain parameters relevant
     *            for the query expander
     */
    public String expandQuery(String query, Map requestParameters);

    /**
     * Returns names of parameters the query expander is expecting to get.
     */
    public String [] getParameterNames();
}
