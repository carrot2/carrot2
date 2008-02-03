/**
 * 
 */
package org.carrot2.core.attribute;

import org.apache.commons.lang.ObjectUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 */
@Root(name = "attribute-metadata")
public class AttributeMetadata
{
    @Element(required = false)
    private String title;

    @Element(required = false)
    private String label;

    @Element(required = false)
    private String description;

    AttributeMetadata()
    {
    }

    AttributeMetadata(String title, String label, String description)
    {
        this.title = title;
        this.label = label;
        this.description = description;
    }

    public String getTitle()
    {
        return title;
    }

    void setTitle(String title)
    {
        this.title = title;
    }

    public String getLabel()
    {
        return label;
    }

    void setLabel(String label)
    {
        this.label = label;
    }

    public String getDescription()
    {
        return description;
    }

    void setDescription(String plainTextDescription)
    {
        this.description = plainTextDescription;
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

        AttributeMetadata other = (AttributeMetadata) obj;

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