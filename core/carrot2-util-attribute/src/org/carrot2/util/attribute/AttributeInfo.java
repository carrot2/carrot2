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
        String group, AttributeLevel level)
    {
        this.key = key;
        this.fieldName = fieldName;
        this.className = className;

        this.javaDoc = javaDoc;
        this.label = label;
        this.title = title;
        this.description = description;
        
        this.group = group;
        this.level = level;
    }
}
