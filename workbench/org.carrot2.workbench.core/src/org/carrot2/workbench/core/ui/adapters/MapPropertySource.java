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
     * {@link #toString()} cache if {@link #label} is not available. 
     */
    private String cache;

    /**
     * Fixed label returned from {@link #toString()} or <code>null</code>.
     */
    private String label;

    /*
     * 
     */
    public MapPropertySource()
    {
    }

    /**
     * Creates a property source for a given set of keys and values. 
     */
    public MapPropertySource(Map properties)
    {
        this(properties, null);
    }

    /**
     * Creates a wrapper map for a given collection of elements.  
     */
    public MapPropertySource(Collection value)
    {
        this(asMap(value, 0), getLabel(value.size()));
    }

    /**
     *  
     */
    protected MapPropertySource(Map properties, String label)
    {
        this.label = label;
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
        if (label != null)
            return;

        // Try to fit all the values if they are small.
        final StringBuilder builder = new StringBuilder();
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
            cache = getLabel(properties.size());
        }
        else
        {
            cache = builder.toString();
        }
    }

    /*
     * 
     */
    private static String getLabel(int size)
    {
        return "[" + size + " element" + (size != 0 ? "s" : "") + "]";
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
                    e.setValue(new MapPropertySource((Collection) value));
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

            /*
             * Create a property descriptor with a read-only cell editor, so that
             * value copying is possible. For mapped property sources, create read-only
             * property editor.
             */
            final PropertyDescriptor descriptor;
            if (newProperties.get(key) instanceof MapPropertySource)
            {
                descriptor = new PropertyDescriptor(key, key.toString());
            }
            else
            {
                descriptor = new ReadOnlyTextPropertyDescriptor(key, key.toString());
            }

            descriptor.setCategory(categoryName);
            descriptors.add(descriptor);
        }
    }

    /**
     * Convert a collection of elements to a {@link Map}, where keys
     * are artificially generated from indices of the elements.
     */
    private static Map asMap(Collection value, final int startIndex)
    {
        final int size = value.size();

        final LinkedHashMap submap = Maps.newLinkedHashMap();
        final int maxDigits = maxDigits(size);
        final String format = "%1$0" + maxDigits + "d";

        /*
         * We want a maximum of MAX_LEVEL_ELEMENTS at each level of the tree.
         */
        final double MAX_LEVEL_ELEMENTS = 10;

        if (size <= MAX_LEVEL_ELEMENTS)
        {
            int index = startIndex;
            for (Object element : (Collection) value)
            {
                submap.put(String.format(format, index), element);
                index++;
            }
        }
        else
        {
            final int levels = (int) Math.ceil(Math.log(value.size()) / Math.log(MAX_LEVEL_ELEMENTS));
            final int increment = (int) Math.pow(MAX_LEVEL_ELEMENTS, levels - 1);

            final ArrayList asArray = new ArrayList(value);

            for (int from = 0; from < asArray.size(); from += increment)
            {
                final int to = Math.min(from + increment, asArray.size());

                final Map slice = asMap(asArray.subList(from, to), from + startIndex);

                submap.put(String.format(format, from + startIndex) 
                    + ".." + String.format(format, to + startIndex - 1), slice);
            }
        }

        return submap;
    }

    /**
     * Return the minimum number of decimal digits that can fully
     * represent a given number.
     */
    private static int maxDigits(int number)
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
        return label != null ? label : cache;
    }
}
