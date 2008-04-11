package org.carrot2.util.attribute;

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
 * {@link AttributeValueSet} (B) and can override some of them. Any depth of the base
 * attribute sets hierarchy is possible.
 */
@Root(name = "value-set")
public class AttributeValueSet
{
    /**
     * Human-readable value of this attribute value set.
     */
    @Element
    public final String label;

    /**
     * Human-readable description of this attribute value set.
     */
    @Element(required = false)
    public final String description;

    /**
     * Holds values of attributes overridden by this attribute set.
     */
    Map<String, Object> overridenAttributeValues;

    /**
     * The base attribute values set, source of all not overridden values.
     */
    AttributeValueSet baseAttributeValueSet;

    /**
     * Identifier of the attribute value set this set is based on. Used only for
     * serialization/ deserialization purposes.
     */
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
    @Root(name = "value")
    static class TypeStringValuePair
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

    /**
     * Creates a new empty {@link AttributeValueSet} with a <code>null</code>
     * description and a <code>null</code> base attribute value set.
     * 
     * @param label human-readable label for this attribute value set
     */
    public AttributeValueSet(String label)
    {
        this(label, null);
    }

    /**
     * Creates a new empty {@link AttributeValueSet} with a <code>null</code>
     * description.
     * 
     * @param label human-readable label for this attribute value set
     * @param base the attribute value set this set should be based on.
     */
    public AttributeValueSet(String label, AttributeValueSet base)
    {
        this(label, null, base);
    }

    /**
     * Creates a new empty {@link AttributeValueSet}.
     * 
     * @param label human-readable label for this attribute value set
     * @param description human-readable description for this attribute value set, can be
     *            <code>null</code>
     * @param base the attribute value set this set should be based on, can be
     *            <code>null</code>.
     */
    public AttributeValueSet(String label, String description, AttributeValueSet base)
    {
        this.label = label;
        this.description = description;

        this.baseAttributeValueSet = base;
        this.overridenAttributeValues = Maps.newHashMap();
    }

    /**
     * Returns value of the attribute with the provided <code>key</code>. Attribute
     * values are resolved in the following order:
     * <ul>
     * <li>If this set contains a value for the attribute with given <code>key</code>
     * set by {{@link #setAttributeValue(String, Object)} or
     * {@link #setAttributeValues(Map)}, the value is returned.</li>
     * <li>Otherwise, if the base attribute value set is not <code>null</code>,
     * attribute value is retrieved from the base set by calling the same method on it. If
     * any of the base attribute sets in the hierarchy contains a value for the provided
     * key, that value is returned.</li>
     * <li>Otherwise, <code>null</code> is returned.</li>
     * </ul>
     * 
     * @param key key of the attribute for which value is to be returned
     * @return value of the attribute or <code>null</code>.
     */
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
     * independent of this {@link AttributeValueSet}, so any modifications to that map
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

    /**
     * Sets a <code>value</code> corresponding to the provided <code>key</code> in
     * this attribute value set. If the set previously contained some value under the
     * provided <code>key</code>, that value is returned. Values set using this method
     * override values found in the base attribute sets of this set.
     * 
     * @param key attribute key
     * @param value attribute value
     * @return previous value of the attribute or <code>null</code>
     */
    public Object setAttributeValue(String key, Object value)
    {
        return overridenAttributeValues.put(key, value);
    }

    /**
     * Copies all <code>values</code> to this attribute value set. If this attribute
     * value set already contains mappings for some of the provided key, the mappings will
     * be overwritten. Values set using this method override values found in the base
     * attribute sets of this set.
     * 
     * @param values values to be set on this attribute value set.
     */
    public void setAttributeValues(Map<String, Object> values)
    {
        overridenAttributeValues.putAll(values);
    }

    /**
     * Converts attribute values to {@link TypeStringValuePair}s for serialization.
     */
    @Persist
    @SuppressWarnings("unused")
    private void convertAttributeValuesToStrings()
    {
        overridenAttributeValuesAsStrings = Maps.newHashMap();
        for (final Map.Entry<String, Object> entry : overridenAttributeValues.entrySet())
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
    @SuppressWarnings("unused")
    private void convertAttributeValuesFromStrings() throws Exception
    {
        overridenAttributeValues = Maps.newHashMap();
        if (overridenAttributeValuesAsStrings == null)
        {
            return;
        }

        for (final Map.Entry<String, TypeStringValuePair> entry : overridenAttributeValuesAsStrings
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
                    final Method valueOfMethod = clazz.getMethod("valueOf", String.class);
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
