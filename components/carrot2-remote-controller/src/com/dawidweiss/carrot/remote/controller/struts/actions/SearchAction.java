
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

package com.dawidweiss.carrot.remote.controller.struts.actions;


import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.*;

import com.dawidweiss.carrot.controller.carrot2.xmlbinding.Query;
import com.dawidweiss.carrot.remote.controller.Carrot2InitServlet;
import com.dawidweiss.carrot.remote.controller.QueryProcessor;
import com.dawidweiss.carrot.remote.controller.process.*;
import com.dawidweiss.carrot.remote.controller.struts.forms.QueryForm;


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

                Query query = new Query(qform.getQuery(), qform.getResultsRequested(), true);

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
