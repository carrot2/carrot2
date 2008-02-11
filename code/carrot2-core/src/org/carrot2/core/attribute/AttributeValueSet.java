package org.carrot2.core.attribute;

import java.lang.reflect.Method;
import java.util.Map;

import org.simpleframework.xml.*;
import org.simpleframework.xml.load.Commit;
import org.simpleframework.xml.load.Persist;

import com.google.common.collect.Maps;

/**
 * Maintains a named set of attribute values. Allows one {@link AttributeValueSet} (A) to
 * be "based" on another {@link AttributeValueSet} (B), whereby the main
 * {@link AttributeValueSet} (A) inherits all values from the base
 * {@link AttributeValueSet} (B) and can override some of them.
 */
@Root(name = "value-set")
public class AttributeValueSet
{
    @Element
    public final String label;

    @Element(required = false)
    public final String description;

    /**
     * Holds values of attributes overriden by this attribute set.
     */
    Map<String, Object> overridenAttributeValues;

    /**
     * The base attribute values set, source of all not overriden values.
     */
    AttributeValueSet baseAttributeValueSet;

    @org.simpleframework.xml.Attribute(name = "based-on", required = false)
    String baseAttributeValueSetId;

    /**
     * This collection is used only for serialization/ deserialization purposes, see
     * {@link #convertAttributeValuesToStrings()} and
     * {@link #convertAttributeValuesFromStrings()}.
     */
    @ElementMap(name = "values", entry = "entry", key = "key", inline = true, attribute = true, required = false)
    private Map<String, TypeStringValuePair> overridenAttributeValuesAsStrings;

    /**
     * When serializing/ deserializing values, we also need to know the original class of
     * the value. In theory, we could look the attribute descriptor up knowing the key of
     * the attribute, but this tight coupling between attribute values sets and
     * descriptors would probably be too cumbersome in most situations.
     */
    @SuppressWarnings("unused")
    @Root(name = "value")
    public static class TypeStringValuePair
    {
        @org.simpleframework.xml.Attribute
        private Class<?> type;

        @org.simpleframework.xml.Attribute(required = false)
        private String value;

        TypeStringValuePair()
        {
        }

        TypeStringValuePair(Class<?> type, String value)
        {
            this.value = value;
            this.type = type;
        }
    }

    AttributeValueSet()
    {
        label = null;
        description = null;
    }

    public AttributeValueSet(String label, String description, AttributeValueSet base)
    {
        this.label = label;
        this.description = description;

        this.baseAttributeValueSet = base;
        this.overridenAttributeValues = Maps.newHashMap();
    }

    public Object getAttributeValue(String key)
    {
        if (overridenAttributeValues.containsKey(key))
        {
            return overridenAttributeValues.get(key);
        }
        else if (baseAttributeValueSet != null)
        {
            return baseAttributeValueSet.getAttributeValue(key);
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns attribute values defined by this {@link AttributeValueSet} and all other
     * {@link AttributeValueSet}s that this set is based on. The returned map is
     * independent of this {@link AttributeValueSet}, so any modifications to the map
     * will not be reflected in this {@link AttributeValueSet}.
     */
    public Map<String, Object> getAttributeValues()
    {
        final Map<String, Object> result = Maps.newHashMap();
        if (baseAttributeValueSet != null)
        {
            result.putAll(baseAttributeValueSet.getAttributeValues());
        }
        result.putAll(overridenAttributeValues);
        return result;
    }

    public Object setAttributeValue(String key, Object value)
    {
        return overridenAttributeValues.put(key, value);
    }

    public void setAttributeValues(Map<String, Object> values)
    {
        overridenAttributeValues.putAll(values);
    }

    /**
     * Converts attribute values to {@link TypeStringValuePair}s for serialization.
     */
    @Persist
    private void convertAttributeValuesToStrings()
    {
        overridenAttributeValuesAsStrings = Maps.newHashMap();
        for (Map.Entry<String, Object> entry : overridenAttributeValues.entrySet())
        {
            if (entry.getValue() != null)
            {
                overridenAttributeValuesAsStrings.put(entry.getKey(),
                    new TypeStringValuePair(entry.getValue().getClass(), entry.getValue()
                        .toString()));
            }
            else
            {
                // Simple XML doesnt seem to be able to handle null entries,
                // so we need to do some hacking here
                overridenAttributeValuesAsStrings.put(entry.getKey(),
                    new TypeStringValuePair(Object.class, null));
            }
        }
    }

    /**
     * Converts attribute values to {@link TypeStringValuePair}s after deserialization.
     */
    @Commit
    private void convertAttributeValuesFromStrings() throws Exception
    {
        overridenAttributeValues = Maps.newHashMap();
        if (overridenAttributeValuesAsStrings == null)
        {
            return;
        }

        for (Map.Entry<String, TypeStringValuePair> entry : overridenAttributeValuesAsStrings
            .entrySet())
        {
            if (entry.getValue().value != null)
            {
                final Class<?> clazz = entry.getValue().type;
                final String stringValue = entry.getValue().value;
                Object value = null;

                // Special support for Class and String
                if (String.class.equals(clazz))
                {
                    value = stringValue;
                }
                else if (Class.class.equals(clazz))
                {
                    value = Class.forName(stringValue
                        .substring(stringValue.indexOf(' ') + 1));
                }
                else
                {
                    // Everything else needs to have a static valueOf(String) method
                    Method valueOfMethod = clazz.getMethod("valueOf", String.class);
                    value = valueOfMethod.invoke(null, stringValue);
                }

                overridenAttributeValues.put(entry.getKey(), value);

            }
            else
            {
                overridenAttributeValues.put(entry.getKey(), null);
            }
        }
    }
}
