
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


import org.apache.struts.action.*;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * This action only fills the Query bean with values and forwards the query to the top frame for
 * further rendering.
 */
public class NewSearchAction
    extends Action
{
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
        ActionForward fwd = mapping.findForward("topframe");
        fwd.setRedirect(false);

        return fwd;
    }
}
