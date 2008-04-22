package org.carrot2.workbench.core.ui.clusters;

import java.util.ArrayList;

import org.carrot2.core.*;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.jobs.ProcessingJob;
import org.carrot2.workbench.core.jobs.ProcessingStatus;
import org.carrot2.workbench.core.ui.IProcessingResultPart;
import org.carrot2.workbench.core.ui.ResultsEditor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.*;

public class ClusterTreeComponent implements IProcessingResultPart
{
    private TreeViewer viewer;
    private IPartListener partListener;
    private IWorkbenchSite site;

    public void init(IWorkbenchSite site, Composite parent, ProcessingJob job)
    {
        initViewer(site, parent);
        job.addJobChangeListener(new JobChangeAdapter()
        {
            @Override
            public void done(IJobChangeEvent event)
            {
                if (event.getResult().getSeverity() == IStatus.OK)
                {
                    final ProcessingResult result =
                        ((ProcessingStatus) event.getResult()).result;
                    setClusters(result);
                }
            }
        });
    }

    public void init(IWorkbenchSite site, Composite parent)
    {
        initViewer(site, parent);
        partListener = new IPartListener()
        {

            public void partActivated(IWorkbenchPart part)
            {
                if (part instanceof ResultsEditor)
                {
                    ResultsEditor resultsEditor = (ResultsEditor) part;
                    if (resultsEditor.getCurrentContent() != null)
                    {
                        setClusters(resultsEditor.getCurrentContent());
                    }
                }
            }

            public void partBroughtToTop(IWorkbenchPart part)
            {
            }

            public void partClosed(IWorkbenchPart part)
            {
            }

            public void partDeactivated(IWorkbenchPart part)
            {
            }

            public void partOpened(IWorkbenchPart part)
            {
                //TODO: attach to property change listener here and refresh if visible editor changes currentContent
            }

        };
        site.getPage().addPartListener(partListener);
    }

    private void initViewer(IWorkbenchSite site, Composite parent)
    {
        viewer = new TreeViewer(parent, SWT.MULTI);
        viewer.setLabelProvider(new ClusterLabelProvider());
        viewer.setContentProvider(new ClusterTreeContentProvider());
        viewer.setInput(new ArrayList<ClusterWithParent>());
        final ClusterSelectionProvider provider = new ClusterSelectionProvider(viewer);
        site.setSelectionProvider(provider);
        this.site = site;
    }

    public Control getControl()
    {
        return viewer.getTree();
    }

    public void dispose()
    {
        if (partListener != null)
        {
            site.getPage().removePartListener(partListener);
        }
    }

    private void setClusters(final ProcessingResult result)
    {
        Utils.asyncExec(new Runnable()
        {
            public void run()
            {
                viewer.setInput(ClusterWithParent.wrap(new ArrayList<Cluster>(result
                    .getClusters())));
            }
        });
    }
}
