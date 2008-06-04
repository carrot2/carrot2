package org.carrot2.workbench.core.ui.attributes;

import java.util.ArrayList;
import java.util.List;

import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.workbench.core.CorePlugin;
import org.carrot2.workbench.core.ui.PropertyProvider;
import org.carrot2.workbench.editors.AttributeChangeEvent;
import org.carrot2.workbench.editors.AttributeChangeListener;
import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class AttributeListComponent extends PropertyProvider
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
            run(true);
        }

        public void run(boolean fireEvents)
        {
            firePropertyChange(IAction.TOOL_TIP_TEXT, getToolTip(!isChecked()),
                getToolTip(isChecked()));
            if (fireEvents && isChecked())
            {
                AttributeListComponent.this.firePropertyChanged(LIVE_UPDATE,
                    !isChecked(), isChecked());
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

    private IAttributesGrouppedControl groupControl;
    private AttributeChangeListener listener;
    private LiveUpdateAction liveUpdateAction;
    private BindableDescriptor descriptor;

    private List<AttributeChangeListener> listeners =
        new ArrayList<AttributeChangeListener>();

    private void createControls(Composite parent)
    {
        groupControl = new SectionGrouppedControl();
        groupControl.init(descriptor);
        groupControl.createMainControl(parent);
        for (Object groupKey : descriptor.attributeGroups.keySet())
        {
            groupControl.createGroup(groupKey);
        }
        if (!descriptor.attributeDescriptors.isEmpty())
        {
            groupControl.createOthers();
        }
    }

    public void init(Composite parent, final BindableDescriptor desc)
    {
        descriptor = desc;
        listener = new AttributeChangeListener()
        {
            public void attributeChange(AttributeChangeEvent event)
            {
                fireAttributeChanged(event);
            }
        };
        createControls(parent);
        groupControl.addAttributeChangeListener(listener);
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

    public boolean isLiveUpdateEnabled()
    {
        return liveUpdateAction.isChecked();
    }

    public void dispose()
    {
        for (AttributesPage page : groupControl.getPages())
        {
            page.removeAttributeChangeListener(listener);
        }
        groupControl.dispose();
    }

    public BindableDescriptor getBindableDescriptor()
    {
        return descriptor;
    }

    void setAttributeValue(String key, Object value, boolean fireEvents)
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
        if (fireEvents)
        {
            listener.attributeChange(new AttributeChangeEvent(this, key, value));
        }
    }

    public void setAttributeValue(String key, Object value)
    {
        setAttributeValue(key, value, true);
    }

    void setLiveUpdate(boolean value, boolean fireEvents)
    {
        liveUpdateAction.setChecked(value);
        liveUpdateAction.run(fireEvents);
    }

    public void setLiveUpdate(boolean enabled)
    {
        setLiveUpdate(enabled, true);
    }

    public void addAttributeChangeListener(AttributeChangeListener listener)
    {
        listeners.add(listener);
    }

    public void removeAttributeChangeListener(AttributeChangeListener listener)
    {
        listeners.remove(listener);
    }

    protected void fireAttributeChanged(AttributeChangeEvent event)
    {
        for (AttributeChangeListener listener : listeners)
        {
            listener.attributeChange(event);
        }
    }
}
