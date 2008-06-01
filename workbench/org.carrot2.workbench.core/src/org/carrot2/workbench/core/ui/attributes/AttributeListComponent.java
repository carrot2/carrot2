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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

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
        BindableDescriptor desc = createBindableDescriptor();
        createControls(parent, desc);
        for (AttributesPage page : groupControl.getPages())
        {
            page.addAttributeChangeListener(listener);
        }
        toolkit.adapt((Composite) groupControl.getControl());
        toolkit.paintBordersFor(groupControl.getControl());
        UiFormUtils.adaptToFormUI(toolkit, groupControl.getControl());
    }

    private void createControls(Composite parent, BindableDescriptor desc)
    {
        groupControl = new ExpandBarGrouppedControl();
        groupControl.init(desc);
        groupControl.createMainControl(parent);
        for (Object groupKey : desc.attributeGroups.keySet())
        {
            groupControl.createGroup(groupKey);
        }
    }

    public void init(Composite parent, final AttributesProvider provider)
    {
        createControls(parent, provider.createBindableDescriptor());
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
