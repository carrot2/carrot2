
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2011, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute.metadata;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.util.Types;

import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Output;

/**
 * Additional information about an attribute field.
 */
public class AttributeFieldInfo
{
    private final Types types;

    private final String key;
    private final AttributeMetadata metadata;
    private final String javaDoc;
    private final VariableElement field;
    private final String declaringClass;
    private final String descriptorClass;
    private final AttributeFieldInfo inherited;
    private final boolean generatesClassSetter;

    AttributeFieldInfo(String attributeKey, AttributeMetadata metadata,
        String javaDoc, VariableElement field, Types types, 
        String declaringClass, String descriptorClass,
        AttributeFieldInfo inherited, boolean generateClassSetter)
    {
        this.types = types;

        this.key = attributeKey;
        this.metadata = metadata;
        this.javaDoc = javaDoc;
        this.field = field;
        this.declaringClass = declaringClass;
        this.descriptorClass = descriptorClass;
        this.inherited = inherited;
        this.generatesClassSetter = generateClassSetter;
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

    public String getDeclaringClass()
    {
        return declaringClass;
    }

    public String getDescriptorClass()
    {
        return descriptorClass;
    }

    public String getLabel()
    {
        return metadata.label;
    }
    
    public String getDescription()
    {
        return metadata.description;
    }
    
    public String getTitle()
    {
        return metadata.title;
    }

    public String getGroup()
    {
        return metadata.getGroup();
    }

    public AttributeLevel getLevel()
    {
        return metadata.getLevel();
    }
    
    public AttributeFieldInfo getInherited()
    {
        return inherited;
    }

    public boolean isGeneratesClassSetter()
    {
        return generatesClassSetter && isInput();
    }

    public boolean isInput()
    {
        return field.getAnnotation(Input.class) != null;
    }

    public boolean isOutput()
    {
        return field.getAnnotation(Output.class) != null;
    }    
    
    public String getType()
    {
        return field.asType().toString();
    }
    
    public String getBoxedType()
    {
        if (field.asType().getKind().isPrimitive())
        {
            return types.boxedClass((PrimitiveType) field.asType()).toString();
        }

        return getType();
    }
}
