package org.carrot2.workbench.core.ui.views;

import java.util.List;

import org.carrot2.core.Cluster;
import org.carrot2.workbench.core.ui.clusters.ClusterTreeComponent;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.*;
import org.eclipse.ui.part.*;

public class ClusterTreeView extends PageBookViewBase
{

    public static final String ID = "org.carrot2.workbench.core.clusters";

    @Override
    protected PageRec doCreatePage(final IWorkbenchPart part)
    {
        if (!(part instanceof IAdaptable))
        {
            return null;
        }
        final ClusterTreeComponent provider =
            (ClusterTreeComponent) ((IAdaptable) part)
                .getAdapter(ClusterTreeComponent.class);
        if (provider == null)
        {
            return null;
        }
        Page partPage = new Page()
        {
            private ClusterTreeComponent tree;
            private transient boolean initialized;

            @Override
            public void createControl(Composite parent)
            {
                tree = new ClusterTreeComponent();
                tree.init(getSite(), parent);
                provider.addPropertyChangeListener(new IPropertyChangeListener()
                {
                    @SuppressWarnings("unchecked")
                    public void propertyChange(PropertyChangeEvent event)
                    {
                        if (event.getProperty().equals(ClusterTreeComponent.CONTENT))
                        {
                            tree.setClusters((List<Cluster>) event.getNewValue());
                            initialized = true;
                        }
                    }
                });
                getSite().getSelectionProvider().addSelectionChangedListener(
                    new ISelectionChangedListener()
                    {
                        public void selectionChanged(SelectionChangedEvent event)
                        {
                            if (initialized)
                            {
                                part.getSite().getSelectionProvider().setSelection(
                                    event.getSelection());
                            }
                        }
                    });
                part.getSite().getSelectionProvider().addSelectionChangedListener(
                    new ISelectionChangedListener()
                    {
                        public void selectionChanged(SelectionChangedEvent event)
                        {
                            if (initialized)
                            {
                                getSite().getSelectionProvider().setSelection(
                                    event.getSelection());
                            }
                        }
                    });
                tree.setClusters(provider.getCurrentContent());
                if (!provider.getCurrentContent().isEmpty())
                {
                    initialized = true;
                }
            }

            @Override
            public Control getControl()
            {
                return tree.getControl();
            }

            @Override
            public void setFocus()
            {
                tree.getControl().setFocus();
            }

            @Override
            public void setActionBars(IActionBars actionBars)
            {
                tree.populateToolbar(actionBars.getToolBarManager());
            }

            @Override
            public void dispose()
            {
                tree.dispose();
            }
        };
        initPage(partPage);
        partPage.createControl(getPageBook());
        return new PageRec(part, partPage);
    }

}
