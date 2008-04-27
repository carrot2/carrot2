package org.carrot2.workbench.core.ui.attributes;

import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.Input;
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
    private AttributesPage page;
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
        page = new AttributesPage(job.algorithm, job.attributes);
        page.ignoreAttributes(AttributeNames.DOCUMENTS);
        page.filterAttributes(Input.class, Processing.class);
        page.init(new IPageSite()
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
        page.createControl(parent);
        page.addAttributeChangeListener(listener);
    }

    public Control getControl()
    {
        return page.getControl();
    }

    public void dispose()
    {
        page.removeAttributeChangeListener(listener);
        page.dispose();
    }
}
