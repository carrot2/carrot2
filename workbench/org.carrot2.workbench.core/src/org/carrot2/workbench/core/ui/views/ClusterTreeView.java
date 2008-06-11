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

public class ClusterTreeView extends PageBookView
{

    public static final String ID = "org.carrot2.workbench.core.clusters";

    @Override
    protected IPage createDefaultPage(PageBook book)
    {
        MessagePage defaultPage = new MessagePage();
        initPage(defaultPage);
        defaultPage.createControl(book);
        defaultPage.setMessage("Nothing to show for this editor");
        return defaultPage;
    }

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
                        }
                    }
                });
                getSite().getSelectionProvider().addSelectionChangedListener(
                    new ISelectionChangedListener()
                    {
                        public void selectionChanged(SelectionChangedEvent event)
                        {
                            part.getSite().getSelectionProvider().setSelection(
                                event.getSelection());
                        }
                    });
                tree.setClusters(provider.getCurrentContent());
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

    @Override
    protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord)
    {
        ((Page) pageRecord.page).dispose();
        pageRecord.dispose();
    }

    @Override
    protected IWorkbenchPart getBootstrapPart()
    {
        return this.getSite().getPage().getActiveEditor();
    }

    @Override
    protected boolean isImportant(IWorkbenchPart part)
    {
        return (part instanceof IEditorPart);
    }

}
