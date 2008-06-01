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

public class AttributeListComponent extends AttributesProvider implements
    IProcessingResultPart
{
    private final class LiveUpdateAction extends Action
    {
        private LiveUpdateAction(String text, int style)
        {
            super(text, style);
        }

        @Override
        public void run()
        {
            runCore();
            AttributeListComponent.this.filePropertyChanged(LIVE_UPDATE, !isChecked(),
                isChecked());
        }

        public void runCore()
        {
            firePropertyChange(IAction.TOOL_TIP_TEXT, getToolTip(!isChecked()),
                getToolTip(isChecked()));
            if (isChecked() && processingJob != null)
            {
                processingJob.schedule();
            }
        }

        @Override
        public ImageDescriptor getImageDescriptor()
        {
            return CorePlugin.getImageDescriptor("icons/synced.gif");
        }

        private String getToolTip(boolean isOn)
        {
            if (isOn)
            {
                return "Live Update On";
            }
            else
            {
                return "Live Update Off";
            }
        }

        @Override
        public String getToolTipText()
        {
            return getToolTip(isChecked());
        }
    }

    //property ids
    public static final String LIVE_UPDATE = "live_update";

    private ProcessingJob processingJob;
    private IAttributesGrouppedControl groupControl;
    private AttributeChangeListener listener;
    private LiveUpdateAction liveUpdateAction;

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
                if (liveUpdateAction.isChecked())
                {
                    processingJob.schedule();
                }
                fireAttributeChanged(event);
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
        BindableDescriptor desc = createBindableDescriptor();
        createControls(parent, pageSite, desc);
        for (AttributesPage page : groupControl.getPages())
        {
            page.addAttributeChangeListener(listener);
        }
        toolkit.adapt((Composite) groupControl.getControl());
        toolkit.paintBordersFor(groupControl.getControl());
        UiFormUtils.adaptToFormUI(toolkit, groupControl.getControl());
    }

    private void createControls(Composite parent, IPageSite pageSite,
        BindableDescriptor desc)
    {
        groupControl = new ExpandBarGrouppedControl();
        groupControl.init(desc, pageSite);
        groupControl.createMainControl(parent);
        for (Object groupKey : desc.attributeGroups.keySet())
        {
            groupControl.createGroup(groupKey);
        }
    }

    public void init(final IPageSite site, Composite parent,
        final AttributesProvider provider)
    {
        createControls(parent, site, provider.createBindableDescriptor());
        groupControl.addAttributeChangeListener(new AttributeChangeListener()
        {
            public void attributeChange(AttributeChangeEvent event)
            {
                fireAttributeChanged(event);
            }
        });
    }

    public void populateToolbar(IToolBarManager manager)
    {
        liveUpdateAction =
            new LiveUpdateAction("Attributes live update", IAction.AS_CHECK_BOX);
        liveUpdateAction.setChecked(true);
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

    @SuppressWarnings("unchecked")
    @Override
    public BindableDescriptor createBindableDescriptor()
    {
        GroupingMethod method = GroupingMethod.GROUP;
        BindableDescriptor desc =
            BindableDescriptorBuilder.buildDescriptor(processingJob.algorithm
                .getExecutableComponent());
        desc = desc.only(Input.class, Processing.class).not(Internal.class).group(method);
        return desc;
    }

    @Override
    public void setAttributeValue(String key, Object value)
    {
        if (groupControl != null)
        {
            for (AttributesPage page : groupControl.getPages())
            {
                if (page.setAttributeValue(key, value))
                {
                    break;
                }
            }
        }
        if (listener != null)
        {
            listener.attributeChange(new AttributeChangeEvent(this, key, value));
        }
    }

    @Override
    public void setPropertyValue(String key, Object value)
    {
        if (key.equals(LIVE_UPDATE))
        {
            liveUpdateAction.setChecked((Boolean) value);
            liveUpdateAction.runCore();
        }
    }
}
