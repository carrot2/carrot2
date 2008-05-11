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
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class ClusterTreeComponent implements IProcessingResultPart
{
    private TreeViewer viewer;
    private ResultsEditor editor;
    private IPropertyListener refresher;

    public void init(IWorkbenchSite site, Composite parent, FormToolkit toolkit,
        ProcessingJob job)
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

    public void init(IWorkbenchSite site, ResultsEditor editor, Composite parent)
    {
        initViewer(site, parent);
        this.editor = editor;
        refresher = new IPropertyListener()
        {
            public void propertyChanged(Object source, int propId)
            {
                if (source instanceof ResultsEditor
                    && propId == ResultsEditor.CURRENT_CONTENT)
                {
                    setClusters(((ResultsEditor) source).getCurrentContent());
                }
            }
        };
        editor.addPropertyListener(refresher);
    }

    private void initViewer(IWorkbenchSite site, Composite parent)
    {
        viewer = new TreeViewer(parent, SWT.MULTI);
        viewer.setLabelProvider(new ClusterLabelProvider());
        viewer.setContentProvider(new ClusterTreeContentProvider());
        viewer.setInput(new ArrayList<ClusterWithParent>());
        final ClusterSelectionProvider provider = new ClusterSelectionProvider(viewer);
        site.setSelectionProvider(provider);
    }

    public void populateToolbar(IToolBarManager manager)
    {
    }

    public Control getControl()
    {
        return viewer.getControl();
    }

    public void dispose()
    {
        if (editor != null)
        {
            editor.removePropertyListener(refresher);
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

    public String getPartName()
    {
        return "Clusters";
    }
}
