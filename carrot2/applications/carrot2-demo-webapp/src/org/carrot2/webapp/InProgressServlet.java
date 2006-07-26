package org.carrot2.webapp;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * A servlet that emits a HTML page with a "in-progress" message
 * and redirects to another URL (contained in the <code>PATH_INFO</code>
 * part of the request URI). 
 * 
 * The servlet accepts GET requests only.
 * 
 * <b>PROBLEMS:</b> 
 * The redirect URI is currently not absolute (and it should be). We can't simply add
 * the name of the local server because this would break the link with Apache (mod_rewrite)
 * 
 * The second problem is that meta redirection is ugly - http://www.w3.org/QA/Tips/reback 
 * 
 * 
 * @author Dawid Weiss
 */
public final class InProgressServlet extends HttpServlet {
    protected void service(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {

        // check if it's a GET method.
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }

        final String qs = request.getQueryString();
        final String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

        // Reconstruct the destination URI
        final String uri =
              request.getContextPath() + pathInfo
            + ((qs == null || "".equals(qs)) ? "" : "?" + qs);

        response.setContentType(Constants.MIME_HTML_CHARSET_UTF);

        final Writer w = response.getWriter();
        w.write(
                "<html><head>"
              + "<meta http-equiv=\"refresh\" content=\"0;URL=" + uri + "\">"
              + "</head>"
              + "<body><table width=\"100%\" height=\"100%\"><tr><td align=\"center\" valign=\"middle\">"
              + "<img src=\"" + request.getContextPath() + "/skins/progress.gif\">"
              + "</td></tr></table>"
              + "</body>"
              + "</html>"
              );
    }
}
