
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute;

/**
 * Human-readable metadata about an attribute. Metadata contains such elements as title,
 * label and description.
 */
public class AttributeMetadata extends CommonMetadata
{
    private String group;
    private AttributeLevel level;

    public AttributeMetadata(String title, String label, String description)
    {
        this(title, label, description, null, null);
    }

    public AttributeMetadata(String title, String label, String description, String group, AttributeLevel level)
    {
        this.title = title;
        this.label = label;
        this.description = description;
        this.group = group;
        this.level = level;
    }

    @Override
    public String toString()
    {
        return "[" + title + ", " + label + ", " + description + "]";
    }

    /**
     * Returns the label of the group this attribute belongs to or <code>null</code> if
     * the attribute is not assigned to any group.
     */
    public String getGroup()
    {
        return group;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }

    /**
     * Returns the attribute level (basic, medium, advanced) or <code>null</code> if the
     * attribute has no level assigned.
     */
    public AttributeLevel getLevel()
    {
        return level;
    }
    
    public void setLevel(AttributeLevel level)
    {
        this.level = level;
    }
}
