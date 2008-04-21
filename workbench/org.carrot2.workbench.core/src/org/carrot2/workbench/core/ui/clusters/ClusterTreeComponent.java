package org.carrot2.workbench.core.ui.clusters;

import java.util.ArrayList;

import org.carrot2.core.*;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.jobs.ProcessingJob;
import org.carrot2.workbench.core.jobs.ProcessingStatus;
import org.carrot2.workbench.core.ui.IProcessingResultPart;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchSite;

public class ClusterTreeComponent implements IProcessingResultPart
{
    private TreeViewer viewer;

    public void init(IWorkbenchSite site, Composite parent, ProcessingJob job)
    {
        viewer = new TreeViewer(parent, SWT.MULTI);
        viewer.setLabelProvider(new ClusterLabelProvider());
        viewer.setContentProvider(new ClusterTreeContentProvider());
        viewer.setInput(new ArrayList<ClusterWithParent>());
        final ClusterSelectionProvider provider = new ClusterSelectionProvider(viewer);
        site.setSelectionProvider(provider);
        job.addJobChangeListener(new JobChangeAdapter()
        {
            @Override
            public void done(IJobChangeEvent event)
            {
                if (event.getResult().getSeverity() == IStatus.OK)
                {
                    final ProcessingResult result =
                        ((ProcessingStatus) event.getResult()).result;
                    Utils.asyncExec(new Runnable()
                    {
                        public void run()
                        {
                            viewer.setInput(ClusterWithParent
                                .wrap(new ArrayList<Cluster>(result.getClusters())));
                        }
                    });
                }
            }
        });
    }

    public Control getControl()
    {
        return viewer.getTree();
    }

    public void dispose()
    {
    }
}
