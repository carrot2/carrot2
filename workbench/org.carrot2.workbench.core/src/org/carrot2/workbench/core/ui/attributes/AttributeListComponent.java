package org.carrot2.workbench.core.ui.attributes;

import java.util.*;

import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
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
            firePropertyChange(IAction.TOOL_TIP_TEXT, getToolTip(!isChecked()),
                getToolTip(isChecked()));
            AttributeListComponent.this.firePropertyChanged(LIVE_UPDATE, !isChecked(),
                isChecked());

        }

        @Override
        public ImageDescriptor getImageDescriptor()
        {
            return WorkbenchCorePlugin.getImageDescriptor("icons/synced.gif");
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

    @Override
    public void dispose()
    {
        for (AttributesPage page : groupControl.getPages())
        {
            page.removeAttributeChangeListener(listener);
        }
        groupControl.dispose();
        listeners.clear();
        super.dispose();
    }

    public BindableDescriptor getBindableDescriptor()
    {
        return descriptor;
    }

    public void setAttributeValue(String key, Object value)
    {
        boolean isChanged = isAttributeValueChanged(key, value);
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
        if (isChanged)
        {
            listener.attributeChange(new AttributeChangeEvent(this, key, value));
        }
    }

    private boolean isAttributeValueChanged(String key, Object value)
    {
        Map<String, Object> attributes = getAttributeValues();
        if (!attributes.containsKey(key))
        {
            return true;
        }
        if (attributes.get(key) == null)
        {
            if (value == null)
            {
                return false;
            }
            return true;
        }
        else
        {
            if (value == null)
            {
                return true;
            }
            return !attributes.get(key).equals(value);
        }
    }

    public Map<String, Object> getAttributeValues()
    {
        HashMap<String, Object> values = new HashMap<String, Object>();
        for (AttributesPage page : groupControl.getPages())
        {
            values.putAll(page.getAttributeValues());
        }
        return Collections.unmodifiableMap(values);
    }

    public void setLiveUpdate(boolean enabled)
    {
        if (liveUpdateAction.isChecked() != enabled)
        {
            liveUpdateAction.setChecked(enabled);
            liveUpdateAction.run();
        }
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
