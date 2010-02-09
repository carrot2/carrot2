
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

package org.carrot2.util.attribute;

import java.util.Collections;
import java.util.Map;

import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

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

    /**
     * Returns the internal (modifiable) map of attribute metadata.
     */
    Map<String, AttributeMetadata> getInternalAttributeMetadata()
    {
        return attributeMetadataInternal;
    }

    /**
     * Sets internal (modifiable) map of attribute metadata.
     */
    void setAttributeMetadata(Map<String, AttributeMetadata> attributeMetadata)
    {
        this.attributeMetadataInternal = attributeMetadata;
    }

    @Override
    public String toString()
    {
        return "[" + title + ", " + label + ", " + description + "]";
    }
}
