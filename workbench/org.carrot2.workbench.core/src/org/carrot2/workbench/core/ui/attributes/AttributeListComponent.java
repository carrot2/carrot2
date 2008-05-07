package org.carrot2.workbench.core.ui.attributes;

import org.carrot2.core.attribute.AttributeNames;
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
import org.eclipse.ui.part.IPageSite;

public class AttributeListComponent implements IProcessingResultPart
{
    private ProcessingJob processingJob;
    private IAttributesGrouppedControl groupControl;
    private AttributeChangeListener listener;

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
        GroupingMethod method = GroupingMethod.STRUCTURE;
        BindableDescriptor desc =
            BindableDescriptorBuilder.buildDescriptor(job.algorithm);
        desc = desc.flatten().group(method);
        groupControl = new ExpandBarGrouppedControl();
        Composite mainControl = groupControl.createMainControl(parent);
        for (Object groupKey : desc.attributeGroups.keySet())
        {
            AttributesPage p1 =
                createPageForGroup(site, mainControl, job, method, groupKey);
            p1.addAttributeChangeListener(listener);
            groupControl.createGroup(groupKey, p1);
        }
    }

    @SuppressWarnings("unchecked")
    private AttributesPage createPageForGroup(final IWorkbenchSite site,
        Composite parent, ProcessingJob job, GroupingMethod method, Object key)
    {
        AttributesPage groupPage = new AttributesPage(job.algorithm, job.attributes);
        groupPage.ignoreAttributes(AttributeNames.DOCUMENTS);
        groupPage.filterAttributes(Input.class, Processing.class);
        groupPage.filterGroup(method, key);
        groupPage.init(new IPageSite()
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

        });
        groupPage.createControl(parent);
        return groupPage;
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
