

/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.remote.controller.struts.actions;


import com.dawidweiss.carrot.remote.controller.*;
import com.dawidweiss.carrot.remote.controller.process.*;
import com.dawidweiss.carrot.remote.controller.struts.forms.*;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.*;
import org.apache.struts.action.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * This action performs proxies query processing to internal application logic.
 */
public class SearchAction
    extends Action
{
    public static final String PROCESSING_RESULTS_KEY = "queryResults";
    public static final String PROCESSING_ERRORS_KEY  = "processingErrors";
    public static final String DEBUGINFO_KEY          = "debugInfo";

    /**
     * Redirect query processing to internal application controller. Compile query object and
     * format the results.
     */
    public ActionForward perform(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response
    )
        throws ServletException
    {
        QueryForm qform = (QueryForm) form;

        try
        {
            if ("".equals(qform.getQuery()) == false)
            {
                // Assemble a Query object and pass it to business logic, the return
                // should be a ResultsHolder object.
                QueryProcessor processor = (QueryProcessor) getServlet().getServletContext()
                                                                .getAttribute(
                        Carrot2InitServlet.CARROT_PROCESSOR_KEY
                    );

                if (processor == null)
                {
                    throw new ServletException("errors.runtime.processor-missing");
                }

                // get the process chain
                ProcessingChainLoader chains = (ProcessingChainLoader) super.getServlet()
                                                                            .getServletContext()
                                                                            .getAttribute(
                        Carrot2InitServlet.CARROT_PROCESSINGCHAINS_LOADER
                    );

                ProcessDefinition processingChain;
                String chainNameKey = qform.getProcessingChain();

                if (chainNameKey == null)
                {
                    List errorsList = new ArrayList(1);
                    errorsList.add(new RuntimeException("errors.runtime.no-processes-defined"));
                    request.setAttribute(PROCESSING_ERRORS_KEY, errorsList);

                    return mapping.findForward("visualize");
                }

                processingChain = chains.findProcessDefinition(chainNameKey);

                if (processingChain == null)
                {
                    throw new ServletException("error.runtime.processing-chain-not-found");
                }

                // We currently buffer all the output. Sucky solution, but
                // works as for now.
                StringWriter buffer = new StringWriter();
                Query query = new Query();
                query.setContent(qform.getQuery());
                query.setRequestedResults(qform.getResultsRequested());

                ProcessingResultHolder result = processor.process(
                        processingChain, query, buffer, request.getSession(true), request,
                        this.getServlet().getServletContext()
                    );

                if ((result != null) && result.isErraneous())
                {
                    // error occurred during processing.
                    request.setAttribute(PROCESSING_ERRORS_KEY, result.getExceptions());
                    request.setAttribute(DEBUGINFO_KEY, result.getDebugInfo());
                    ActionForward fwd = mapping.findForward("processingError");
                    fwd.setRedirect(false);
                    return fwd;
                }
                else
                {
                    request.setAttribute(PROCESSING_RESULTS_KEY, buffer);
                }
            }
            else
            {
                // no query -- show 'void' page.
                return mapping.findForward("welcome");
            }
        }
        catch (Throwable t)
        {
            request.setAttribute("exception", t);

            return mapping.findForward("runtimeError");
        }

        return mapping.findForward("visualize");
    }
}
