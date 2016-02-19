
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.editors.impl;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.carrot2.util.StringUtils;
import org.carrot2.util.attribute.Required;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.workbench.editors.AttributeEditorInfo;

import org.carrot2.shaded.guava.common.collect.*;

/**
 * An editor for any fields that have {@link ImplementingClasses} annotation. The field is
 * initialized with an instance of one of the classes listed in
 * {@link ImplementingClasses#classes()}.
 */
public final class ImplementingClassesEditor extends MappedValueComboEditor
{
    /**
     * The constraint.
     */
    private ImplementingClasses constraint;

    /*
     * 
     */
    @Override
    protected AttributeEditorInfo init(Map<String,Object> defaultValues)
    {
        for (Annotation ann : descriptor.constraints)
        {
            if (ann instanceof ImplementingClasses)
            {
                constraint = (ImplementingClasses) ann;
            }
        }

        if (constraint == null)
        {
            throw new RuntimeException("Missing constraint: " + ImplementingClasses.class);
        }

        valueRequired = (descriptor.getAnnotation(Required.class) != null);

        final BiMap<Object, String> valueToName = HashBiMap.create();
        final List<Object> valueOrder = Lists.newArrayList();
        for (Class<?> clazz : constraint.classes())
        {
            valueOrder.add(clazz);
            valueToName.put(clazz, StringUtils.splitCamelCase(ClassUtils.getShortClassName(clazz)));
        }
        setMappedValues(valueToName, valueOrder);

        return new AttributeEditorInfo(1, false);
    }

    /*
     * 
     */
    @Override
    public void setValue(Object newValue)
    {
        if (newValue != null && !(newValue instanceof Class<?>))
        {
            newValue = newValue.getClass();
        }
        
        super.setValue(newValue);
    }
}
