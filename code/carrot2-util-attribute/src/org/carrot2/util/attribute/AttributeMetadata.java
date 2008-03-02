package org.carrot2.util.attribute;

import org.simpleframework.xml.Root;

/**
 * Human-readable metadata about an attribute. Metadata contains such elements as title,
 * label and description.
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
    public String toString()
    {
        return "[" + title + ", " + label + ", " + description + "]";
    }
}