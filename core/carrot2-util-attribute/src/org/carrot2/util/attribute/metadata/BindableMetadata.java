
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Bindable;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Persister;

/**
 * Human-readable metadata for a {@link Bindable} type.
 */
@Root(name = "component-metadata")
public class BindableMetadata extends CommonMetadata
{
    @ElementMap(name = "attributes", entry = "attribute", key = "field-name", inline = false, attribute = true)
    private Map<String, AttributeMetadata> attributeMetadataInternal; 

    BindableMetadata()
    {
    }

    /**
     * Returns metadata for all attributes in the bindable type.
     * 
     * @return metadata for all attributes in the bindable type. Key in the map represents
     *         the attribute key as defined by {@link Attribute#key()}. The returned map
     *         is unmodifiable.
     */
    public Map<String, AttributeMetadata> getAttributeMetadata()
    {
        return Collections.unmodifiableMap(attributeMetadataInternal);
    }

    void setAttributeMetadata(Map<String, AttributeMetadata> map)
    {
        attributeMetadataInternal = map;
    }

    @Override
    public String toString()
    {
        return "[" + title + ", " + label + ", " + description + "]";
    }

    /**
     * Load metadata of a given bindable class, merged with bindable attributes from 
     * parent classes marked with {@link Bindable} as well.
     */
    public static BindableMetadata forClassWithParents(final Class<?> clazz)
    {
        final BindableMetadata bindable = getBindableMetadata(clazz);

        for (final Class<?> bindableClass : 
            getClassesFromBindableHerarchy(clazz.getSuperclass()))
        {
            final BindableMetadata moreMetadata = getBindableMetadata(bindableClass);
            bindable.attributeMetadataInternal.putAll(
                moreMetadata.getAttributeMetadata());
        }

        return bindable;
    }

    /**
     * Returns all {@link Bindable} from the hierarchy of the provided <code>clazz</code>,
     * including <code>clazz</code>.
     */
    static Collection<Class<?>> getClassesFromBindableHerarchy(Class<?> clazz)
    {
        final Collection<Class<?>> classes = new ArrayList<Class<?>>();

        while (clazz != null)
        {
            if (clazz.getAnnotation(Bindable.class) != null)
            {
                classes.add(clazz);
            }

            clazz = clazz.getSuperclass();
        }

        return classes;
    }

    /**
     * Deserializes metadata for a {@link Bindable} class.
     */
    private static BindableMetadata getBindableMetadata(final Class<?> clazz)
    {
        BindableMetadata bindableMetadata = null;
        InputStream inputStream = null;
        try
        {
            final String name = clazz.getName() + ".xml";
            inputStream = clazz.getClassLoader().getResourceAsStream(
                name);
            if (inputStream == null)
                throw new IOException("No such resource: " + name);

            bindableMetadata = new Persister().read(BindableMetadata.class,
                inputStream);
            
            // Quickfix for a bug in SimpleXML: https://sourceforge.net/tracker/?func=detail&atid=661526&aid=3043930&group_id=112203
            for (Map.Entry<String, AttributeMetadata> e 
                : bindableMetadata.attributeMetadataInternal.entrySet())
            {
                if (e.getValue() == null)
                    e.setValue(new AttributeMetadata());
            }
        }
        catch (final Exception e)
        {
            throw new RuntimeException("Could not load attribute metadata for: "
                + clazz, e);
        }
        finally
        {
            try
            {
                if (inputStream != null) inputStream.close();
            }
            catch (IOException e)
            {
                // ignore.
            }
        }

        return bindableMetadata;
    }
}
