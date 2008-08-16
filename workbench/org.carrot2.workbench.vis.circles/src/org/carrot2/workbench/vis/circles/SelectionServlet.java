package org.carrot2.workbench.vis.circles;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.*;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.ui.SearchEditor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.google.common.collect.Lists;

/**
 * HTTP callback servlet for reacting to group selection in the visualization code. 
 */
@SuppressWarnings("serial")
public class SelectionServlet extends HttpServlet
{
    /**
     * Base class for dispatching {@link CirclesViewPage} callback
     * actions.
     */
    private static abstract class PageAction implements Runnable
    {
        private final int page;

        public PageAction(int page)
        {
            this.page = page;
        }
        
        public final void run()
        {
            final CirclesViewPage viewPage = CirclesView.getActiveCirclesViewPage(page);
            if (viewPage != null)
            {
                final ProcessingResult pr = viewPage.editor
                    .getSearchResult().getProcessingResult();

                run(viewPage, pr);
            }
        }

        protected abstract void run(CirclesViewPage viewPage, ProcessingResult pr);

        /**
         * Dispatch in the UI thread.
         */
        public final static void syncExec(PageAction action)
        {
            PlatformUI.getWorkbench().getDisplay().syncExec(action);
        }
    }
    
    /**
     * 
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        HttpServletUtils.sendNoCache(resp);

        final String action = req.getParameter("action");
        final Integer page = HttpServletUtils.getIntParameter(req, "page");

        if (action == null || page == null)
        {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        if ("group".equals(action))
        {
            setGroupSelection(req, resp, page);
        }
        else if ("clear".equals(action))
        {
            clearSelection(req, resp, page);
        }
        else if ("document".equals(action))
        {
            documentSelection(req, resp, page);
        }
        else
        {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        resp.sendError(HttpServletResponse.SC_OK);
    }

    /*
     * 
     */
    private void documentSelection(HttpServletRequest req, HttpServletResponse resp,
        final Integer page) throws IOException
    {
        final Integer documentId = HttpServletUtils.getIntParameter(req, "document");
        if (documentId == null)
        {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        PageAction.syncExec(new PageAction(page) {
            protected void run(CirclesViewPage viewPage, ProcessingResult pr)
            {
                if (pr != null)
                {
                    for (Document d : pr.getDocuments())
                    {
                        if (documentId.equals(d.getId()))
                        {
                            final String url = d.getField(Document.CONTENT_URL);
                            if (!StringUtils.isEmpty(url))
                            {
                                openURL(url);
                            }
                        }
                    }
                }
            }
            
            private void openURL(String location)
            {
                try
                {
                    WorkbenchCorePlugin.getDefault().getWorkbench().getBrowserSupport()
                        .createBrowser(
                            IWorkbenchBrowserSupport.AS_EDITOR
                                | IWorkbenchBrowserSupport.LOCATION_BAR
                                | IWorkbenchBrowserSupport.NAVIGATION_BAR
                                | IWorkbenchBrowserSupport.STATUS, null, null, null)
                        .openURL(new URL(location));
                }
                catch (Exception e)
                {
                    Utils.logError("Couldn't open internal browser", e, false);
                }
            }
        });
    }

    /*
     * 
     */
    private void setGroupSelection(HttpServletRequest req, HttpServletResponse resp, 
        final Integer page) throws IOException
    {
        final Integer group = HttpServletUtils.getIntParameter(req, "group");
        if (group == null)
        {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        PageAction.syncExec(new PageAction(page) {
            protected void run(CirclesViewPage viewPage, ProcessingResult pr)
            {
                if (pr != null)
                {
                    setSelection(pr, viewPage.editor, group);
                }
            }

            private void setSelection(ProcessingResult pr, SearchEditor editor, int groupId)
            {
                final List<Cluster> clusters = pr.getClusters();

                if (clusters != null && !clusters.isEmpty())
                {
                    ClusterWithParent c = 
                        ClusterWithParent.find(groupId, ClusterWithParent.wrap(clusters));

                    if (c != null)
                    {
                        /*
                         * Construct full tree path to allow automatic expansion of 
                         * tree selection observers.
                         */
                        final ArrayList<ClusterWithParent> path = Lists.newArrayList();
                        while (c != null)
                        {
                            path.add(0, c);
                            c = c.parent;
                        }

                        editor.setSelection(
                            new StructuredSelection(new TreePath(path.toArray())));
                    }
                }
            }
        });
    }

    /*
     * 
     */
    private void clearSelection(HttpServletRequest req, HttpServletResponse resp, 
        final Integer page)
        throws IOException
    {
        PageAction.syncExec(new PageAction(page) {
            protected void run(CirclesViewPage viewPage, ProcessingResult pr)
            {
                viewPage.editor.setSelection(StructuredSelection.EMPTY);
            }
        });
    }
}
