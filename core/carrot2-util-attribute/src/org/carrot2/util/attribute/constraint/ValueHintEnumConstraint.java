package org.carrot2.util.attribute.constraint;

import java.lang.annotation.Annotation;
import java.util.*;

import org.simpleframework.xml.Root;

import com.google.common.collect.BiMap;
import com.google.common.collect.Maps;

@Root(name = "value-hint")
public class ValueHintEnumConstraint extends Constraint
{
    /**
     * A map of valid values and their constants.
     */
    private Map<String, Enum<?>> valueSet;

    /**
     * If <code>true</code>, the attribute's value must be one of the constants in
     * {@link #valueSet}.
     */
    private boolean strict;

    /*
     * 
     */
    ValueHintEnumConstraint()
    {
        // Hide from the public view.
    }

    /*
     * 
     */
    protected boolean isMet(Object value)
    {
        checkAssignableFrom(CharSequence.class, value);

        /*
         * null satisfies this constraints. Use NotBlank for null-testing.
         */
        if (value == null)
        {
            return true;
        }

        /*
         * If not strict, this is only a hint, so skip checking.
         */
        if (!strict)
        {
            return true;
        }

        final String asString = ((CharSequence) value).toString();
        return valueSet.containsKey(asString);
    }

    /*
     * 
     */
    @Override
    public String toString()
    {
        return "value-hint";
    }

    /*
     * 
     */
    @Override
    protected void populateCustom(Annotation annotation)
    {
        try
        {
            final ValueHintEnum valueHint = (ValueHintEnum) annotation;
            this.strict = valueHint.strict();
            this.valueSet = getValidValuesMap(valueHint);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a bidirectional mapping between valid attribute values (keys) and their
     * enum constants (values).
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Enum<?>> getValidValuesMap(ValueHintEnum annotation)
    {
        final LinkedHashMap<String, Enum<?>> valueSet = Maps.newLinkedHashMap();

        final EnumSet<? extends Enum<?>> set = EnumSet.allOf((Class) annotation.values());
        for (Enum<?> e : set)
        {
            String valid = e.name();
            if (e instanceof ValueHintMapping)
            {
                valid = ((ValueHintMapping) e).getAttributeValue();
            }

            valueSet.put(valid, e);
        }

        return valueSet;
    }

    /**
     * Returns a bidirectional mapping between valid attribute values (keys) and
     * user-friendly names (values).
     */
    @SuppressWarnings("unchecked")
    public static BiMap<String, String> getValueToFriendlyName(ValueHintEnum annotation)
    {
        final BiMap<String, String> valueToName = Maps.newHashBiMap();

        final EnumSet<? extends Enum<?>> set = EnumSet.allOf((Class) annotation.values());
        for (Enum<?> e : set)
        {
            String value = e.name();
            String name = e.toString();

            if (e instanceof ValueHintMapping)
            {
                value = ((ValueHintMapping) e).getAttributeValue();
                name = ((ValueHintMapping) e).getUserFriendlyName();
            }

            valueToName.put(value, name);
        }

        return valueToName;
    }
}
