package org.carrot2.util.attribute;

import org.simpleframework.xml.Element;

/**
 * Common metadata items for {@link BindableMetadata} and {@link AttributeMetadata}.
 */
public class CommonMetadata
{
    @Element(required = false)
    protected String title;

    @Element(required = false)
    protected String label;

    @Element(required = false)
    protected String description;

    /**
     * A one sentence summary of the element. Could be presented as a header of the tool
     * tip of the corresponding UI component.
     */
    public String getTitle()
    {
        return title;
    }

    protected void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * A short label for the element which can be presented as the label of the
     * corresponding UI component.
     */
    public String getLabel()
    {
        return label;
    }

    protected void setLabel(String label)
    {
        this.label = label;
    }

    /**
     * A longer, possibly multi sentence, description of the element. Could be presented
     * as a body of the tool tip of the corresponding UI component.
     */
    public String getDescription()
    {
        return description;
    }

    protected void setDescription(String plainTextDescription)
    {
        this.description = plainTextDescription;
    }
}
