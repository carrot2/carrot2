

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.controller.carrot2.struts.actions;


import com.dawidweiss.carrot.controller.carrot2.struts.forms.*;
import org.apache.struts.action.*;
import javax.servlet.http.*;


/**
 * This action changes the current user's interface language/
 */
public class ReconfigureAction
    extends Action
{
    public ActionForward perform(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response
    )
    {
        ConfigurationForm config = (ConfigurationForm) form;

        // Alter user interface configuration.
        super.setLocale(request, new java.util.Locale(config.getLocale(), ""));

        return mapping.findForward("ok");
    }
}
