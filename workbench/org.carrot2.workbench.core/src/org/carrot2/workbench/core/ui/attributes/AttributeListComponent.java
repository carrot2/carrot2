package org.carrot2.workbench.core.ui.attributes;

import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.core.jobs.ProcessingJob;
import org.carrot2.workbench.core.ui.IProcessingResultPart;
import org.carrot2.workbench.editors.AttributeChangeEvent;
import org.carrot2.workbench.editors.AttributeChangeListener;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.IPageSite;

public class AttributeListComponent implements IProcessingResultPart
{
    private ProcessingJob processingJob;
    private IAttributesGrouppedControl groupControl;
    private AttributeChangeListener listener;
    private FormToolkit toolkit;

    @SuppressWarnings("unchecked")
    public void init(final IWorkbenchSite site, Composite parent, FormToolkit toolkit,
        ProcessingJob job)
    {
        this.toolkit = toolkit;
        this.processingJob = job;
        listener = new AttributeChangeListener()
        {
            public void attributeChange(AttributeChangeEvent event)
            {
                processingJob.attributes.put(event.key, event.value);
                processingJob.schedule();
            }
        };
        IPageSite pageSite = new IPageSite()
        {

            public IActionBars getActionBars()
            {
                return null;
            }

            public void registerContextMenu(String menuId, MenuManager menuManager,
                ISelectionProvider selectionProvider)
            {
            }

            public IWorkbenchPage getPage()
            {
                return site.getPage();
            }

            public ISelectionProvider getSelectionProvider()
            {
                return site.getSelectionProvider();
            }

            public Shell getShell()
            {
                return site.getShell();
            }

            public IWorkbenchWindow getWorkbenchWindow()
            {
                return site.getWorkbenchWindow();
            }

            public void setSelectionProvider(ISelectionProvider provider)
            {
            }

            public Object getAdapter(Class adapter)
            {
                return site.getAdapter(adapter);
            }

            public Object getService(Class api)
            {
                return site.getService(api);
            }

            public boolean hasService(Class api)
            {
                return site.hasService(api);
            }

        };
        GroupingMethod method = GroupingMethod.GROUP;
        BindableDescriptor desc =
            BindableDescriptorBuilder.buildDescriptor(job.algorithm
                .getExecutableComponent());
        desc = desc.only(Input.class, Processing.class).not(Internal.class).group(method);
        groupControl = new ExpandBarGrouppedControl();
        groupControl.init(desc, pageSite);
        groupControl.createMainControl(parent);
        toolkit.adapt((Composite) groupControl.getControl());
        for (Object groupKey : desc.attributeGroups.keySet())
        {
            groupControl.createGroup(groupKey);
        }
        for (AttributesPage page : groupControl.getPages())
        {
            page.addAttributeChangeListener(listener);
        }
        toolkit.paintBordersFor(groupControl.getControl());
        adaptToFormUI(groupControl.getControl());
    }

    /**
     * Calls {@link FormToolkit#adapt(Control, boolean, boolean)} for given control. If
     * <code>control</code> is instance of {@link Composite}, than this method is
     * called for all the children.
     * 
     * @param control
     */
    public void adaptToFormUI(Control control)
    {
        if (control instanceof Composite)
        {
            Composite c = (Composite) control;
            toolkit.adapt(c);
            for (int i = 0; i < c.getChildren().length; i++)
            {
                Control child = c.getChildren()[i];
                adaptToFormUI(child);
            }
        }
        else
        {
            toolkit.adapt(control, true, true);
        }
    }

    public Control getControl()
    {
        return groupControl.getControl();
    }

    public void dispose()
    {
        for (AttributesPage page : groupControl.getPages())
        {
            page.removeAttributeChangeListener(listener);
        }
        groupControl.dispose();
    }

    public String getPartName()
    {
        return "Attributes";
    }
}
