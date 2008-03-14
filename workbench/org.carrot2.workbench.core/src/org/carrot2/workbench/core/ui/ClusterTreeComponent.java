package org.carrot2.workbench.core.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.carrot2.core.*;
import org.carrot2.workbench.core.CorePlugin;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.jobs.ProcessingJob;
import org.carrot2.workbench.core.jobs.ProcessingStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchSite;

//TODO: I think there's enough inner classes here, to make it separate namespace 
//and devide it into separate files
public class ClusterTreeComponent
{
    private class ClusterSelectionProvider implements ISelectionProvider
    {
        public ClusterSelectionProvider()
        {
            viewer.addSelectionChangedListener(new ISelectionChangedListener()
            {

                public void selectionChanged(SelectionChangedEvent event)
                {
                    SelectionChangedEvent event2 =
                        new SelectionChangedEvent(ClusterSelectionProvider.this,
                            getSelection());
                    fireSelectionChanged(event2);
                }

            });
        }

        List<ISelectionChangedListener> listeners =
            new ArrayList<ISelectionChangedListener>();

        public void addSelectionChangedListener(ISelectionChangedListener listener)
        {
            listeners.add(listener);
        }

        public ISelection getSelection()
        {
            IStructuredSelection selectionWithParent =
                (IStructuredSelection) viewer.getSelection();
            List<ClusterWithParent> listWithParent = selectionWithParent.toList();
            List<Cluster> list = new ArrayList<Cluster>();
            for (ClusterWithParent clusterWithParent : listWithParent)
            {
                list.add(clusterWithParent.cluster);
            }
            return new StructuredSelection(list);
        }

        public void removeSelectionChangedListener(ISelectionChangedListener listener)
        {
            listeners.remove(listener);
        }

        public void setSelection(ISelection selection)
        {
            throw new NotImplementedException();
        }

        private void fireSelectionChanged(SelectionChangedEvent event)
        {
            for (ISelectionChangedListener listener : listeners)
            {
                listener.selectionChanged(event);
            }
        }

    };

    private final class ClusterTreeContentProvider implements ITreeContentProvider
    {
        public Object [] getChildren(Object parentElement)
        {
            return ((ClusterWithParent) parentElement).subclusters
                .toArray(new ClusterWithParent [0]);
        }

        public Object getParent(Object element)
        {
            return ((ClusterWithParent) element).parent;
        }

        public boolean hasChildren(Object element)
        {
            List<ClusterWithParent> children = ((ClusterWithParent) element).subclusters;
            return (children != null && !children.isEmpty());
        }

        @SuppressWarnings("unchecked")
        public Object [] getElements(Object inputElement)
        {
            return ((List<ClusterWithParent>) inputElement).toArray();
        }

        public void dispose()
        {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
        }
    }

    private final class ClusterLabelProvider extends LabelProvider
    {
        Image folderImage =
            CorePlugin.getImageDescriptor("icons/folder.gif").createImage();

        @Override
        public String getText(Object element)
        {
            Cluster cluster = ((ClusterWithParent) element).cluster;
            return String.format("%s (%d)", cluster.getLabel(), cluster.size());
        }

        @Override
        public Image getImage(Object element)
        {
            return folderImage;
        }

        @Override
        public void dispose()
        {
            folderImage.dispose();
            super.dispose();
        }
    }

    private TreeViewer viewer;

    public ClusterTreeComponent(IWorkbenchSite site, Composite parent, ProcessingJob job)
    {
        viewer = new TreeViewer(parent, SWT.MULTI);
        viewer.setLabelProvider(new ClusterLabelProvider());
        viewer.setContentProvider(new ClusterTreeContentProvider());
        viewer.setInput(new ArrayList<ClusterWithParent>());
        final ClusterSelectionProvider provider = new ClusterSelectionProvider();
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
}
