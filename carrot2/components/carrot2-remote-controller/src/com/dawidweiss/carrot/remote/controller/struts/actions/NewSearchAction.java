

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
