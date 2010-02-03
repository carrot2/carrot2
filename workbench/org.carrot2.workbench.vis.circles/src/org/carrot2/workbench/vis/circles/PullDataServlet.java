
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.vis.circles;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.carrot2.core.ProcessingResult;
import org.carrot2.util.CloseableUtils;
import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.ui.PlatformUI;

/**
 * HTTP servlet serving active editor's {@link ProcessingResult} as
 * an XML.
 */
@SuppressWarnings("serial")
public class PullDataServlet extends HttpServlet
{
    /**
     * Serve XML content from the editor identified by the <code>page</code>
     * parameter.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        HttpServletUtils.sendNoCache(resp);

        final Integer page = HttpServletUtils.getIntParameter(req, "page");
        if (page == null)
        {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        final ProcessingResult pr = getProcessingResult(page); 
        if (pr == null)
        {
            // No processing result anymore.
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        else
        {
            resp.setContentType("text/xml; charset=UTF-8");
            final OutputStream w = resp.getOutputStream();
            try
            {
                pr.serialize(w, true, true);
            }
            catch (Exception e)
            {
                Utils.logError("Error serializing XML results.", e, false);
            }
            CloseableUtils.close(w);
        }
    }

    /**
     * Locate active {@link CirclesView} and extract {@link ProcessingResult}
     * from the given view's page. 
     */
    private ProcessingResult getProcessingResult(final int page)
    {
        final ProcessingResult [] result = new ProcessingResult [1];

        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() 
        {
            public void run()
            {
                final CirclesViewPage viewPage = CirclesView.getActiveCirclesViewPage(page);
                if (viewPage != null)
                {
                    result[0] = viewPage.editor
                        .getSearchResult().getProcessingResult();
                }
            }
        });

        return result[0];
    }
}
