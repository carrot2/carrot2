package org.carrot2.workbench.core.ui.clusters;

import java.util.*;

import org.carrot2.core.Cluster;
import org.carrot2.core.ClusterWithParent;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.ui.PropertyProvider;
import org.carrot2.workbench.core.ui.ResultsEditor;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchSite;

import com.google.common.collect.Lists;

public class ClusterTreeComponent extends PropertyProvider
{
    public static final String CONTENT = "content";

    private TreeViewer viewer;
    private ResultsEditor editor;
    private IPropertyListener refresher;
    private List<Cluster> currentContent = Lists.newArrayList();

    public void init(IWorkbenchSite site, Composite parent)
    {
        initViewer(site, parent);
    }

    private void initViewer(IWorkbenchSite site, Composite parent)
    {
        viewer = new TreeViewer(parent, SWT.NONE);
        viewer.setLabelProvider(new ClusterLabelProvider());
        viewer.setContentProvider(new ClusterTreeContentProvider());
        viewer.setInput(new ArrayList<ClusterWithParent>());
        viewer.setAutoExpandLevel(2);
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

    public void setClusters(final List<Cluster> clusters)
    {
        Utils.asyncExec(new Runnable()
        {
            public void run()
            {
                final List<ClusterWithParent> wrappedClusters = ClusterWithParent
                    .wrap(clusters);
                viewer.setInput(wrappedClusters);
                if (!wrappedClusters.isEmpty())
                {
                    viewer.setSelection(new StructuredSelection(wrappedClusters.get(0)));
                }
                Object oldContent = currentContent;
                currentContent = clusters;
                firePropertyChanged(CONTENT, oldContent, currentContent);
            }
        });
    }

    public List<Cluster> getCurrentContent()
    {
        return Collections.unmodifiableList(currentContent);
    }
}
