/**
 *
 */
package org.carrot2.util.attribute.metadata;

import org.simpleframework.xml.Element;

/**
 *
 */
public class CommonMetadata
{
    @Element(required = false)
    protected String title;

    @Element(required = false)
    protected String label;

    @Element(required = false)
    protected String description;

    public String getTitle()
    {
        return title;
    }

    protected void setTitle(String title)
    {
        this.title = title;
    }

    public String getLabel()
    {
        return label;
    }

    protected void setLabel(String label)
    {
        this.label = label;
    }

    public String getDescription()
    {
        return description;
    }

    protected void setDescription(String plainTextDescription)
    {
        this.description = plainTextDescription;
    }
}
