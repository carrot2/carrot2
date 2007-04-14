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
