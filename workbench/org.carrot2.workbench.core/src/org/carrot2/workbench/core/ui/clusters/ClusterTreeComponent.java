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
                Cluster root = new Cluster("All topics", clusters);
                ClusterWithParent wrappedRoot = ClusterWithParent.wrap(null, root);
                viewer.setInput(Lists.newArrayList(wrappedRoot));
                viewer.setSelection(new StructuredSelection(wrappedRoot));
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
