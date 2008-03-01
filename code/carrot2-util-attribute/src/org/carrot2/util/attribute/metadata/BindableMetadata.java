/**
 *
 */
package org.carrot2.util.attribute.metadata;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

/**
 *
 */
@Root(name = "component-metadata")
public class BindableMetadata extends CommonMetadata
{
    @ElementMap(name = "attributes", entry = "attribute", key = "field-name", inline = false, attribute = true)
    private Map<String, AttributeMetadata> attributeMetadataInternal;

    BindableMetadata()
    {
    }

    public Map<String, AttributeMetadata> getAttributeMetadata()
    {
        return Collections.unmodifiableMap(attributeMetadataInternal);
    }

    Map<String, AttributeMetadata> getInternalAttributeMetadata()
    {
        return attributeMetadataInternal;
    }

    void setAttributeMetadata(Map<String, AttributeMetadata> attributeMetadata)
    {
        this.attributeMetadataInternal = attributeMetadata;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj == null || !(obj instanceof AttributeMetadata))
        {
            return false;
        }

        final CommonMetadata other = (CommonMetadata) obj;

        return ObjectUtils.equals(title, other.title)
            && ObjectUtils.equals(label, other.label)
            && ObjectUtils.equals(description, other.description);
    }

    @Override
    public int hashCode()
    {
        return ObjectUtils.hashCode(title) ^ ObjectUtils.hashCode(label)
            ^ ObjectUtils.hashCode(description);
    }

    @Override
    public String toString()
    {
        return "[" + title + ", " + label + ", " + description + "]";
    }
}
