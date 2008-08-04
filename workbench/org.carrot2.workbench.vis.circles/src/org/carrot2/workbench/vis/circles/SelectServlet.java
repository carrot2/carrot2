package org.carrot2.workbench.vis.circles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.carrot2.core.Cluster;
import org.carrot2.core.ClusterWithParent;
import org.carrot2.core.ProcessingResult;
import org.carrot2.workbench.core.ui.SearchEditor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.Lists;

/**
 * HTTP callback servlet for reacting to group selection in the visualization code. 
 */
@SuppressWarnings("serial")
public class SelectServlet extends HttpServlet
{
    /**
     * 
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        HttpServletUtils.sendNoCache(resp);

        final Integer page = HttpServletUtils.getIntParameter(req, "page");
        final Integer group = HttpServletUtils.getIntParameter(req, "group");

        if (page == null || group == null)
        {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() 
        {
            public void run()
            {
                final CirclesViewPage viewPage = CirclesView.getActiveCirclesViewPage(page);
                if (viewPage != null)
                {
                    final ProcessingResult pr = viewPage.editor.getSearchResult()
                        .getProcessingResult();
                    
                    if (pr != null)
                    {
                        setSelection(pr, viewPage.editor, group);
                    }
                }
            }
        });

        resp.sendError(HttpServletResponse.SC_OK);
    }
    
    /**
     * 
     */
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
}
