package org.carrot2.workbench.core.ui.attributes;

import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.core.CorePlugin;
import org.carrot2.workbench.core.jobs.ProcessingJob;
import org.carrot2.workbench.core.ui.IProcessingResultPart;
import org.carrot2.workbench.core.ui.UiFormUtils;
import org.carrot2.workbench.editors.AttributeChangeEvent;
import org.carrot2.workbench.editors.AttributeChangeListener;
import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.ImageDescriptor;
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
    private volatile boolean liveUpdate = true;

    @SuppressWarnings("unchecked")
    public void init(final IWorkbenchSite site, Composite parent, FormToolkit toolkit,
        ProcessingJob job)
    {
        this.processingJob = job;
        listener = new AttributeChangeListener()
        {
            public void attributeChange(AttributeChangeEvent event)
            {
                processingJob.attributes.put(event.key, event.value);
                if (liveUpdate)
                {
                    processingJob.schedule();
                }
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
        UiFormUtils.adaptToFormUI(toolkit, groupControl.getControl());
    }

    public void populateToolbar(IToolBarManager manager)
    {
        IAction liveUpdateAction =
            new Action("Attributes live update", IAction.AS_CHECK_BOX)
            {
                @Override
                public void run()
                {
                    String tooltip = getToolTipText();
                    liveUpdate = !liveUpdate;
                    if (liveUpdate)
                    {
                        processingJob.schedule();
                    }
                    firePropertyChange(IAction.TOOL_TIP_TEXT, tooltip, getToolTipText());
                }

                @Override
                public ImageDescriptor getImageDescriptor()
                {
                    return CorePlugin.getImageDescriptor("icons/synced.gif");
                }

                @Override
                public String getToolTipText()
                {
                    if (liveUpdate)
                    {
                        return "Live Update On";
                    }
                    else
                    {
                        return "Live Update Off";
                    }
                }
            };
        liveUpdateAction.setChecked(liveUpdate);
        manager.add(liveUpdateAction);
        manager.update(true);
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
