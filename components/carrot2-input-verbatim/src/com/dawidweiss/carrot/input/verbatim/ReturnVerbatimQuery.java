
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.input.verbatim;


import org.apache.log4j.Logger;
import java.io.*;
import javax.servlet.http.*;


/**
 * This input processor class returns the query as a result.
 */
public class ReturnVerbatimQuery
    extends com.dawidweiss.carrot.input.InputRequestProcessor
{
    private final Logger log = Logger.getLogger(this.getClass());

    /**
     * Processes the query and writes the result to the output stream.
     *
     * @param query The query which will be copied to the output.
     * @param output The stream, where the query will be saved to.
     * @param requestedResultsNumber The requested number of results (doesn't matter in this
     *        component).
     * @param request HttpRequest which caused this processing. not used by this component.
     */
    public void processQuery(
        String query, int requestedResultsNumber, Writer output, HttpServletRequest request
    )
    {
        try
        {
            log.debug(
                "Received query [" + query.length() + " bytes]: "
                + query.substring(0, Math.min(80, query.length()))
            );

            output.write(query);
        }
        catch (IOException e)
        {
        }
    }
}
