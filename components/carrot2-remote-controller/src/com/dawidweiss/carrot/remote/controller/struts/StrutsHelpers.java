

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


package com.dawidweiss.carrot.remote.controller.struts;


import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.apache.struts.util.*;
import java.util.*;
import javax.servlet.jsp.PageContext;


/**
 * This class provides some static support methods for STRUTS.
 */
public final class StrutsHelpers
{
    private static final Logger log = Logger.getLogger(StrutsHelpers.class);

    private StrutsHelpers()
    {
    }

    public static MessageResources getMessageResources(PageContext pageContext)
    {
        // Acquire the resources object containing our messages
        MessageResources resources = (MessageResources) pageContext.getAttribute(
                Action.MESSAGES_KEY, PageContext.APPLICATION_SCOPE
            );

        return resources;
    }


    public static Locale getDefaultPageLocale(PageContext pageContext)
    {
        Locale locale;

        try
        {
            locale = (Locale) pageContext.getAttribute(
                    Action.LOCALE_KEY, PageContext.SESSION_SCOPE
                );
        }
        catch (IllegalStateException e)
        {
            locale = null;
        }

        return locale;
    }


    public static String getMessage(PageContext pageContext, String key)
    {
        MessageResources mres = getMessageResources(pageContext);

        if (mres == null)
        {
            log.warn("MessageResources object not present in page context.");

            return key;
        }
        else
        {
            String message = mres.getMessage(getDefaultPageLocale(pageContext), key);

            if (message == null)
            {
                return key;
            }
            else
            {
                return message;
            }
        }
    }


    public static String getMessageOrDefault(
        PageContext pageContext, String key, String defaultValue
    )
    {
        MessageResources mres = getMessageResources(pageContext);

        if (mres == null)
        {
            log.warn("MessageResources object not present in page context.");

            return defaultValue;
        }

        String value = mres.getMessage(getDefaultPageLocale(pageContext), key);

        if (value == null)
        {
            return defaultValue;
        }
        else
        {
            return value;
        }
    }
}
