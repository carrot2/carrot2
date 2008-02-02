/**
 * 
 */
package org.carrot2.core.attribute.metadata;


/**
 *
 */
public class AttributeMetadata
{
    private String title;
    private String label;
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