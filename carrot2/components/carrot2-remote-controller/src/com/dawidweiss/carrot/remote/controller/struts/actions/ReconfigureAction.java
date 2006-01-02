
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
package com.dawidweiss.carrot.remote.controller.struts.actions;


import com.dawidweiss.carrot.remote.controller.struts.forms.*;
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
