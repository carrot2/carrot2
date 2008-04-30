package org.carrot2.util.attribute;

import java.lang.reflect.Method;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * A helper class for serializing/ deserializing collection of objects using Simple XML.
 */
public class TypeStringValuePair
{
    /** Type of the value to be serialized. */
    @org.simpleframework.xml.Attribute
    private Class<?> type;

    /** Value as string. */
    @org.simpleframework.xml.Attribute(required = false)
    private String value;

    public TypeStringValuePair()
    {
    }

    public TypeStringValuePair(Class<?> type, String value)
    {
        this.value = value;
        this.type = type;
    }

    /**
     * Converts a map of objects of arbitrary types into a map of
     * {@link TypeStringValuePair}s.
     */
    public static Map<String, TypeStringValuePair> toTypeStringValuePairs(
        Map<String, Object> source)
    {
        Map<String, TypeStringValuePair> result = Maps.newLinkedHashMap();
        for (final Map.Entry<String, Object> entry : source.entrySet())
        {
            if (entry.getValue() != null)
            {
                result.put(entry.getKey(), new TypeStringValuePair(entry.getValue()
                    .getClass(), entry.getValue().toString()));
            }
            else
            {
                // Simple XML doesnt seem to be able to handle null entries,
                // so we need to do some hacking here
                result.put(entry.getKey(), new TypeStringValuePair(Object.class, null));
            }
        }

        return result;
    }

    /**
     * Converts a map of {@link TypeStringValuePair}s into a map of objects of the
     * corresponding types. Looks for the <code>valueOf(String)</code> method in the
     * target type and uses the method to perform the conversion from {@link String}.
     */
    public static Map<String, Object> fromTypeStringValuePairs(
        Map<String, Object> target, Map<String, TypeStringValuePair> source)
        throws Exception
    {
        for (final Map.Entry<String, TypeStringValuePair> entry : source.entrySet())
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

                target.put(entry.getKey(), value);

            }
            else
            {
                target.put(entry.getKey(), null);
            }
        }

        return target;
    }
}