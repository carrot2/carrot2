/**
 * 
 */
package org.carrot2.core.attribute;

import org.apache.commons.lang.ObjectUtils;
import org.simpleframework.xml.Root;

/**
 *
 */
@Root(name = "attribute-metadata")
public class AttributeMetadata extends CommonMetadata
{
    AttributeMetadata()
    {
    }

    AttributeMetadata(String title, String label, String description)
    {
        this.title = title;
        this.label = label;
        this.description = description;
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

        CommonMetadata other = (CommonMetadata) obj;

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