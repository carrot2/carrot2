package org.carrot2.util.attribute.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.ObjectUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.Constraint;


public class AttributeDescriptor
{
    public final AttributeMetadata metadata;

    public final String key;
    public final Class<?> type;
    public final Object defaultValue;
    public final Constraint constraint;

    public final boolean initAttribute;
    public final boolean processingAttribute;
    public final boolean inputAttribute;
    public final boolean outputAttribute;
    
    public final boolean requiredAttribute;

    AttributeDescriptor(Field field, Object defaultValue, Constraint constraint,
        AttributeMetadata metadata)
    {
        this.key = BindableUtils.getKey(field);
        this.type = ClassUtils.primitiveToWrapper(field.getType());
        this.defaultValue = defaultValue;
        this.constraint = constraint;
        this.metadata = metadata;

        this.initAttribute = field.getAnnotation(Init.class) != null;
        this.processingAttribute = field.getAnnotation(Processing.class) != null;
        this.inputAttribute = field.getAnnotation(Input.class) != null;
        this.outputAttribute = field.getAnnotation(Output.class) != null;
        
        this.requiredAttribute = field.getAnnotation(Required.class) != null;
    }

    boolean hasBindingAnnotation(Class<? extends Annotation> annotationClass)
    {
        if (Init.class.equals(annotationClass) && initAttribute)
        {
            return true;
        }
        else if (Processing.class.equals(annotationClass) && processingAttribute)
        {
            return true;
        }
        else if (Input.class.equals(annotationClass) && inputAttribute)
        {
            return true;
        }
        else if (Output.class.equals(annotationClass) && outputAttribute)
        {
            return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj == null || !(obj instanceof AttributeDescriptor))
        {
            return false;
        }

        final AttributeDescriptor other = ((AttributeDescriptor) obj);
        return other.key.equals(this.key) && other.type.equals(this.type)
            && ObjectUtils.equals(defaultValue, defaultValue)
            && ObjectUtils.equals(other.constraint, this.constraint)
            && ObjectUtils.equals(metadata, other.metadata);
    }

    @Override
    public int hashCode()
    {
        return key.hashCode();
    }

    @Override
    public String toString()
    {
        return key + "=" + type;
    }
}
