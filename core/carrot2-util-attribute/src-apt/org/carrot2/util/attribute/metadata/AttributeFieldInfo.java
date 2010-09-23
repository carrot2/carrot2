package org.carrot2.util.attribute.metadata;

import javax.lang.model.element.VariableElement;

/**
 * Additional information about an attribute field.
 */
public class AttributeFieldInfo
{
    public final String key;
    public final AttributeMetadata metadata;
    public final String javaDoc;
    public final VariableElement field;

    AttributeFieldInfo(String attributeKey, AttributeMetadata metadata,
        String javaDoc, VariableElement field)
    {
        this.key = attributeKey;
        this.metadata = metadata;
        this.javaDoc = javaDoc;
        this.field = field;
    }

    /**
     * Returns the key of this attribute.
     */
    public String getKey()
    {
        return key;
    }
    
    public String getJavaDoc()
    {
        return javaDoc;
    }

    public VariableElement getField()
    {
        return field;
    }
}
