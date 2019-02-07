
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
 * Description of an {@link Attribute} of a {@link Bindable} type, including
 * javadoc documentation and compile-time extracted tags.
 */
public final class AttributeInfo
{
    /**
     * Attribute key.
     */
    public final String key;
    
    /**
     * Name of the declaring field.
     */
    public final String fieldName;

    /**
     * Name of the declaring class.
     */
    public final String className;

    /**
     * Complete JavaDoc.
     */
    public final String javaDoc;
    
    /**
     * Attribute label.
     */
    public final String label;
    
    /**
     * Attribute title.
     */
    public final String title;
    
    /**
     * Attribute description (javadoc excerpt).
     */
    public final String description;
    
    /**
     * Attribute group in the user interface.
     */
    public final String group;

    /**
     * Attribute level in the user interface.
     */
    public final AttributeLevel level;

    /*
     * 
     */
    public AttributeInfo(String key, String className, String fieldName, 
        String javaDoc, String label, String title, String description,
        String group, AttributeLevel level, AttributeInfo inheritFrom)
    {
        this.fieldName = fieldName;
        this.className = className;

        this.key = key;
        if (inheritFrom == null)
        {
            this.javaDoc = javaDoc;
            this.label = label;
            this.title = title;
            this.description = description;

            this.group = group;
            this.level = level;
        }
        else
        {
            this.javaDoc = firstNonNull(javaDoc, inheritFrom.javaDoc);
            this.label = firstNonNull(label, inheritFrom.label);
            this.title = firstNonNull(title, inheritFrom.title);
            this.description = firstNonNull(description, inheritFrom.description);

            this.group = firstNonNull(group, inheritFrom.group);
            this.level = firstNonNull(level, inheritFrom.level);
        }
    }

    /**
     * Returns the first non-null argument from the list or null if all arguments are null.
     */
    @SafeVarargs
    private static <T> T firstNonNull(T... objs)
    {
        for (T t : objs) {
            if (t != null) {
                return t;
            }
        }
        return null;
    }
}
