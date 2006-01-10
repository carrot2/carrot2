
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


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.*;

import com.dawidweiss.carrot.remote.controller.Carrot2InitServlet;
import com.dawidweiss.carrot.remote.controller.QueryProcessor;


/**
 * This action clears the query caches (all read-write caches).
 */
public class CacheClearAction
    extends Action
{
    public ActionForward perform(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws ServletException
    {
        // Assemble a Query object and pass it to business logic, the return
        // should be a ResultsHolder object.
        final QueryProcessor processor = (QueryProcessor) getServlet()
            .getServletContext().getAttribute(Carrot2InitServlet.CARROT_PROCESSOR_KEY);

        if (processor == null) {
            throw new ServletException("errors.runtime.processor-missing");
        }
        
        processor.clearCache();

        return mapping.findForward("ok");
    }
}
