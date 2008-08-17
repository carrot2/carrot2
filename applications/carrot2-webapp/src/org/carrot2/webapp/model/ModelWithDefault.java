package org.carrot2.webapp.model;

import java.util.Collection;

import org.simpleframework.xml.Attribute;

/**
 *
 */
public class ModelWithDefault
{
    @Attribute(name = "default", required = false)
    public boolean isDefault;

    public static <T extends ModelWithDefault> T getDefault(Collection<T> entries)
    {
        T first = null;
        for (T entry : entries)
        {
            if (first == null)
            {
                first = entry;
            }

            if (entry.isDefault)
            {
                return entry;
            }
        }

        return first;
    }
}
