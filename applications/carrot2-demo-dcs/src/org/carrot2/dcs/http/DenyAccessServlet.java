
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

package org.carrot2.dcs.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

/**
 * A servlet that denies access to the mapped resources.
 */
public final class DenyAccessServlet extends HttpServlet
{
    /**
     * Intercept all methods.
     */
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
        IOException
    {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access forbidden.");
    }
}
