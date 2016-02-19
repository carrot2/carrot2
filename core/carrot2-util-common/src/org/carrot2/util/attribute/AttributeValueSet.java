
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute;

import java.util.Map;
import java.util.TreeMap;

import org.carrot2.util.simplexml.*;
import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persist;

import org.carrot2.shaded.guava.common.collect.Maps;

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
     * Human-readable value of this attribute value set. <b>Only for read-only use.</b>
     */
    @Element
    public String label;

    /**
     * Human-readable description of this attribute value set. <b>Only for read-only use.</b>
     */
    @Element(required = false)
    public String description;

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
    @ElementMap(entry = "attribute", key = "key", attribute = true, inline = true, required = false)
    private TreeMap<String, SimpleXmlWrapperValue> overridenAttributeValuesForSerialization;

    AttributeValueSet()
    {
    }

    /**
     * Creates a new empty {@link AttributeValueSet} with a <code>null</code> description
     * and a <code>null</code> base attribute value set.
     * 
     * @param label human-readable label for this attribute value set
     */
    public AttributeValueSet(String label)
    {
        this(label, null);
    }

    /**
     * Creates a new empty {@link AttributeValueSet} with a <code>null</code> description.
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
     * Returns value of the attribute with the provided <code>key</code>. Attribute values
     * are resolved in the following order:
     * <ul>
     * <li>If this set contains a value for the attribute with given <code>key</code> set
     * by {{@link #setAttributeValue(String, Object)} or {@link #setAttributeValues(Map)},
     * the value is returned.</li>
     * <li>Otherwise, if the base attribute value set is not <code>null</code>, attribute
     * value is retrieved from the base set by calling the same method on it. If any of
     * the base attribute sets in the hierarchy contains a value for the provided key,
     * that value is returned.</li>
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
     * Sets a <code>value</code> corresponding to the provided <code>key</code> in this
     * attribute value set. If the set previously contained some value under the provided
     * <code>key</code>, that value is returned. Values set using this method override
     * values found in the base attribute sets of this set.
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
     * Copies all <code>values</code> to this attribute value set. If this attribute value
     * set already contains mappings for some of the provided key, the mappings will be
     * overwritten. Values set using this method override values found in the base
     * attribute sets of this set.
     * 
     * @param values values to be set on this attribute value set.
     */
    public void setAttributeValues(Map<String, Object> values)
    {
        overridenAttributeValues.putAll(values);
    }

    /**
     * Returns attribute values from the provided {@link AttributeValueSet} or
     * <code>null</code> if the provided {@link AttributeValueSet} is <code>null</code>.
     */
    public static Map<String, Object> getAttributeValues(
        AttributeValueSet attributeValueSet)
    {
        return attributeValueSet != null ? attributeValueSet.getAttributeValues() : null;
    }

    /**
     * Converts attribute values to {@link ISimpleXmlWrapper}s for serialization.
     */
    @Persist
    private void convertAttributeValuesToStrings()
    {
        overridenAttributeValuesForSerialization = new TreeMap<String, SimpleXmlWrapperValue>(
            String.CASE_INSENSITIVE_ORDER);
        overridenAttributeValuesForSerialization.putAll(SimpleXmlWrappers
            .wrap(overridenAttributeValues));
    }

    /**
     * Converts attribute values to {@link ISimpleXmlWrapper}s after deserialization.
     */
    @Commit
    private void convertAttributeValuesFromStrings() throws Exception
    {
        if (overridenAttributeValuesForSerialization == null)
        {
            overridenAttributeValues = Maps.newHashMap();
        }
        else
        {
            overridenAttributeValues = SimpleXmlWrappers
                .unwrap(overridenAttributeValuesForSerialization);
        }
    }

    /*
     * 
     */
    @Override
    public String toString()
    {
        final StringBuilder b = new StringBuilder();
        b.append("AttributeValueSet [");

        boolean first = true;
        for (Map.Entry<String, Object> e : getAttributeValues().entrySet())
        {
            if (!first) b.append(", ");
            b.append(e.getKey() != null ? e.getKey() : "null");
            b.append('=');
            b.append(e.getValue() != null ? e.getValue().toString() : "null");
            first = false;
        }
        b.append("]");

        return b.toString();
    }
}
