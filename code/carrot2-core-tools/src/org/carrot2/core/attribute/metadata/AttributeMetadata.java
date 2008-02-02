/**
 * 
 */
package org.carrot2.core.attribute.metadata;

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
    public String toString()
    {
        return "[" + title + ", " + label + ", " + description + "]";
    }
}