package org.carrot2.workbench.core.ui.adapters;

import java.util.*;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.views.properties.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A {@link IPropertySource2} that displays properties from a {@link Map}.
 */
@SuppressWarnings("unchecked")
public class MapPropertySource implements IPropertySource2
{
    /**
     * Max length of preview if possible to render.
     */
    private final static int MAX_LENGTH = 120;

    /*
     * 
     */
    private final LinkedHashMap<Object, Object> properties = Maps.newLinkedHashMap();

    /*
     * 
     */
    private final List<IPropertyDescriptor> descriptors = Lists.newArrayList();

    /**
     * {@link #toString()} cache. 
     */
    private String cache;

    /*
     * 
     */
    public MapPropertySource()
    {
    }
    
    /*
     * 
     */
    public MapPropertySource(Map properties)
    {
        add(properties, null);
    }
    
    /**
     * Add new properties to this map. Property keys must not overlap.
     * 
     * @param categoryName Optional class name, can be <code>null</code>.
     */
    public void add(Map newProperties, String categoryName)
    {
        final LinkedHashMap clonedProperties = new LinkedHashMap(newProperties);

        buildDescriptors(clonedProperties, categoryName);
        properties.putAll(clonedProperties);

        refreshCache();
    }

    /**
     * 
     */
    private void refreshCache()
    {
        // Try to fit all the values if they are small.
        StringBuilder builder = new StringBuilder();
        for (Object key : properties.keySet())
        {
            if (builder.length() > 0) builder.append(", ");

            builder.append(key);
            builder.append('=');
            builder.append(getPropertyValue(key));
            
            if (builder.length() > MAX_LENGTH)
            {
                break;
            }
        }

        if (builder.length() > MAX_LENGTH)
        {
            cache = "[" + properties.size() 
                + " element" + (properties.size() != 0 ? "s" : "") + "]";
        }
        else
        {
            cache = builder.toString();
        }
    }

    /*
     * 
     */
    public boolean isPropertyResettable(Object id)
    {
        return false;
    }

    /*
     * 
     */
    public boolean isPropertySet(Object id)
    {
        return properties.get(id) != null;
    }

    /*
     * 
     */
    public Object getEditableValue()
    {
        return this;
    }

    /*
     * 
     */
    public IPropertyDescriptor [] getPropertyDescriptors()
    {
        return descriptors.toArray(new IPropertyDescriptor[descriptors.size()]);
    }

    /*
     * 
     */
    public Object getPropertyValue(Object id)
    {
        return properties.get(id) == null ? "[null]" : properties.get(id);
    }

    /*
     * 
     */
    public void resetPropertyValue(Object id)
    {
    }

    /*
     * 
     */
    public void setPropertyValue(Object id, Object value)
    {
    }
    
    /*
     * 
     */
    private void buildDescriptors(Map<Object, Object> newProperties, String categoryName)
    {
        final IAdapterManager adapterManager = Platform.getAdapterManager();
        for (Map.Entry<Object, Object> e : newProperties.entrySet())
        {
            final Object key = e.getKey();
            final Object value = e.getValue();

            /*
             * Expand complex properties into sub-elements.
             */
            if (value != null)
            {
                if (value instanceof Map)
                {
                    e.setValue(new MapPropertySource((Map) value));
                }
                else
                if (value instanceof Collection)
                {
                    final LinkedHashMap submap = Maps.newLinkedHashMap();
                    
                    final int maxDigits = maxDigits(((Collection) value).size());
                    final String format = "index %1$0" + maxDigits + "d";
                    int index = 0;
                    for (Object element : (Collection) value)
                    {
                        submap.put(String.format(format, index), element);
                        index++;
                    }
                    e.setValue(new MapPropertySource(submap));
                }
                else
                {
                    final IPropertySource adapted = (IPropertySource) 
                        adapterManager.getAdapter(value, IPropertySource.class);
                    if (adapted != null)
                    {
                        e.setValue(adapted);
                    }
                }
            }

            final PropertyDescriptor descriptor = new PropertyDescriptor(key, key.toString());
            descriptor.setCategory(categoryName);
            descriptors.add(descriptor);
        }
    }

    /*
     * 
     */
    private int maxDigits(int number)
    {
        int digits = 1;
        while ((number / 10) != 0)
        {
            number = number / 10;
            digits++;
        }
        return digits;
    }

    /*
     * 
     */
    @Override
    public String toString()
    {
        return cache;
    }
}
