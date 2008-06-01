package org.carrot2.workbench.core.ui.clusters;

import java.util.ArrayList;

import org.carrot2.core.*;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.ui.ResultsEditor;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchSite;

import com.google.common.collect.Lists;

public class ClusterTreeComponent// implements IProcessingResultPart
{
    private TreeViewer viewer;
    private ResultsEditor editor;
    private IPropertyListener refresher;

    public void init(IWorkbenchSite site, Composite parent)
    {
        initViewer(site, parent);
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

    public void setClusters(final ProcessingResult result)
    {
        Utils.asyncExec(new Runnable()
        {
            public void run()
            {
                viewer.setInput(Lists.newArrayList(ClusterWithParent.wrap(null,
                    new Cluster("All topics", result.getClusters()))));
            }
        });
    }

    public String getPartName()
    {
        return "Clusters";
    }
}
