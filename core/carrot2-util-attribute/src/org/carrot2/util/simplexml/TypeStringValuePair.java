package org.carrot2.util.simplexml;

import java.lang.reflect.Method;
import java.util.*;

import org.simpleframework.xml.*;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * A helper class for serializing/ deserializing collections of objects of different types
 * using Simple XML.
 */
@Root(name = "value")
public class TypeStringValuePair
{
    /** Optional key for the value */
    @Attribute(required = false)
    private String key;

    /** Type of the value to be serialized. */
    @Attribute
    private Class<?> type;

    /** Value as string. */
    @Element(required = false, data = true)
    private String value;

    public TypeStringValuePair()
    {
    }

    public TypeStringValuePair(Class<?> type, String key, String value)
    {
        this.value = value;
        this.type = type;
        this.key = key;
    }

    /**
     * Converts a map of objects of arbitrary types into a list of
     * {@link TypeStringValuePair}s.
     */
    public static List<TypeStringValuePair> toTypeStringValuePairs(
        Map<String, ?> source, String... ignoredKeys)
    {
        final Set<String> ignored = ImmutableSet.of(ignoredKeys);

        List<TypeStringValuePair> result = Lists.newArrayList();
        for (final Map.Entry<String, ?> entry : source.entrySet())
        {
            if (ignored.contains(entry.getKey()))
            {
                continue;
            }

            /*
             * There are two special cases handled here. First, Simple XML library does
             * not handle null entries. Second, enums need to be handled separately since
             * their toString() method can (and should) be overriden to implement more
             * human-friendly strings.
             */

            if (entry.getValue() == null)
            {
                // Simple XML hack.
                result.add(new TypeStringValuePair(Object.class, entry.getKey(), null));
                continue;
            }

            if (entry.getValue() instanceof Enum)
            {
                // Enum handling.
                result.add(new TypeStringValuePair(entry.getValue().getClass(), entry
                    .getKey(), ((Enum<?>) entry.getValue()).name()));
                continue;
            }

            // All the remaining types.
            result.add(new TypeStringValuePair(entry.getValue().getClass(), entry
                .getKey(), entry.getValue().toString()));
        }

        return result;
    }

    /**
     * Converts a list of {@link TypeStringValuePair}s into a map of objects of the
     * corresponding types. Looks for the <code>valueOf(String)</code> method in the
     * target type and uses the method to perform the conversion from {@link String}.
     */
    public static Map<String, Object> fromTypeStringValuePairs(
        Map<String, Object> target, List<TypeStringValuePair> source) throws Exception
    {
        for (TypeStringValuePair entry : source)
        {
            if (entry.key == null)
            {
                throw new IllegalArgumentException("No key specified for value: "
                    + entry.value);
            }

            if (entry.value != null)
            {
                final Class<?> clazz = entry.type;
                final String stringValue = entry.value;
                Object value = null;

                // Special support for Class and String
                if (String.class.equals(clazz))
                {
                    value = stringValue;
                }
                else if (Class.class.equals(clazz))
                {
                    value = Thread.currentThread().getContextClassLoader().loadClass(
                        stringValue.substring(stringValue.indexOf(' ') + 1));
                }
                else
                {
                    // TODO: See bug: http://issues.carrot2.org/browse/CARROT-395
                    // Everything else needs to have a static valueOf(String) method
                    final Method valueOfMethod = clazz.getMethod("valueOf", String.class);
                    value = valueOfMethod.invoke(null, stringValue.trim());
                }

                target.put(entry.key, value);

            }
            else
            {
                target.put(entry.key, null);
            }
        }

        return target;
    }
}