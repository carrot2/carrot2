
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute.constraint;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.*;

/**
 * Utilities for dealing with {@link IValueHintMapping} and {@link Enum} classes.
 */
public final class ValueHintMappingUtils
{
    private ValueHintMappingUtils()
    {
        // no instances.
    }

    /**
     * Returns a bidirectional mapping between valid attribute values (keys) and their
     * enum constants (values). Keys in the returned map are ordered according to enum's
     * declaration.
     */
    public static Map<String, Enum<?>> getValidValuesMap(Class<? extends Enum<?>> clazz)
    {
        final LinkedHashMap<String, Enum<?>> valueSet = Maps.newLinkedHashMap();
        for (Enum<?> e : clazz.getEnumConstants())
        {
            String value = e.name();
            if (e instanceof IValueHintMapping)
            {
                value = ((IValueHintMapping) e).getAttributeValue();
            }

            valueSet.put(value, e);
        }

        return valueSet;
    }

    /**
     * Returns a bidirectional mapping between valid attribute values (keys) and
     * user-friendly names (values).
     */
    public static BiMap<String, String> getValueToFriendlyName(Class<? extends Enum<?>> clazz)
    {
        final BiMap<String, String> valueToName = HashBiMap.create();

        for (Enum<?> e : clazz.getEnumConstants())
        {
            String value = e.name();
            String name = e.toString();

            if (e instanceof IValueHintMapping)
            {
                value = ((IValueHintMapping) e).getAttributeValue();
                name = ((IValueHintMapping) e).getUserFriendlyName();
            }

            valueToName.put(value, name);
        }

        return valueToName;
    }
}
