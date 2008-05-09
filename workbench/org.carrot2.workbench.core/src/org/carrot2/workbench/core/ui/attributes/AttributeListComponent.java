package org.carrot2.workbench.core.ui.attributes;

import org.carrot2.core.attribute.*;
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
import org.eclipse.ui.part.IPageSite;

import com.google.common.collect.Lists;

public class AttributeListComponent implements IProcessingResultPart
{
    private ProcessingJob processingJob;
    private IAttributesGrouppedControl groupControl;
    private AttributeChangeListener listener;

    @SuppressWarnings("unchecked")
    public void init(final IWorkbenchSite site, Composite parent, ProcessingJob job)
    {
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
        GroupingMethod method = GroupingMethod.STRUCTURE;
        BindableDescriptor desc = BindableDescriptorBuilder
            .buildDescriptor(job.algorithm);
        desc = desc.only(Input.class, Processing.class).not(Internal.class).group(method);
        groupControl = new ExpandBarGrouppedControl();
        groupControl.init(job.algorithm);
        groupControl.createMainControl(parent);
        for (Object groupKey : desc.attributeGroups.keySet())
        {
            groupControl.createGroup(groupKey, desc, pageSite);
        }
        for (AttributesPage page : groupControl.getPages())
        {
            page.addAttributeChangeListener(listener);
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
}
