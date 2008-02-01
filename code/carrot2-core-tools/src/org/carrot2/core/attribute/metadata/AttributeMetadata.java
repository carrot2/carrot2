/**
 * 
 */
package org.carrot2.core.attribute.metadata;

import org.apache.commons.lang.ObjectUtils;
import org.carrot2.util.StringUtils;

/**
 *
 */
public class AttributeMetadata
{
    private String title;
    private String label;
    private String plainTextDescription;

    public AttributeMetadata()
    {
    }

    public AttributeMetadata(String title, String label, String plainTextDescription)
    {
        this.title = title;
        this.label = label;
        this.plainTextDescription = plainTextDescription;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getPlainTextDescription()
    {
        return plainTextDescription;
    }

    public void setPlainTextDescription(String plainTextDescription)
    {
        this.plainTextDescription = plainTextDescription;
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
            && ObjectUtils.equals(plainTextDescription, other.plainTextDescription);
    }

    @Override
    public int hashCode()
    {
        return StringUtils.multiStringHashCode(title, label, plainTextDescription);
    }

    @Override
    public String toString()
    {
        return "[" + title + ", " + label + ", " + plainTextDescription + "]";
    }
}