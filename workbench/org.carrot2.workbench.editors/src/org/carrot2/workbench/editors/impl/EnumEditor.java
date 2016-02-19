
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

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.carrot2.util.attribute.Required;
import org.carrot2.util.attribute.constraint.*;
import org.carrot2.workbench.editors.AttributeEditorInfo;

/**
 * Editor for mapped values (enumerated types and unrestricted strings with enum hints).
 */
public final class EnumEditor extends MappedValueComboEditor
{
    /*
     * 
     */
    @SuppressWarnings("unchecked")
    @Override
    public AttributeEditorInfo init(Map<String,Object> defaultValues)
    {
        Class<? extends Enum<?>> clazz = (Class<? extends Enum<?>>) descriptor.type;
        if (clazz.isEnum())
        {
            valueRequired = (descriptor.getAnnotation(Required.class) != null);
            anyValueAllowed = false;

            super.setMappedValues(ValueHintMappingUtils.getValueToFriendlyName(clazz),
                new ArrayList<Object>(ValueHintMappingUtils.getValidValuesMap(clazz)
                    .keySet()));
        }
        else if (String.class.equals(clazz))
        {
            final ValueHintEnum hint = descriptor.getAnnotation(ValueHintEnum.class);

            if (hint == null)
            {
                throw new IllegalArgumentException("Editor applicable to Strings with "
                    + ValueHintEnum.class.getName() + " annotation: "
                    + descriptor);
            }
            
            clazz = hint.values();

            valueRequired = (descriptor.getAnnotation(Required.class) != null);
            anyValueAllowed = true;

            super.setMappedValues(ValueHintMappingUtils.getValueToFriendlyName(clazz),
                new ArrayList<Object>(ValueHintMappingUtils.getValidValuesMap(clazz)
                    .keySet()));
        }
        else
        {
            throw new IllegalArgumentException("Attribute type not supported: "
                + descriptor);
        }

        return new AttributeEditorInfo(1, false);
    }

    /*
     * 
     */
    @Override
    public void setValue(Object newValue)
    {
        if (ObjectUtils.equals(newValue, getValue()))
        {
            return;
        }

        final String asString;
        if (newValue == null)
        {
            asString = null;
        }
        else if (newValue instanceof IValueHintMapping)
        {
            asString = ((IValueHintMapping) newValue).getAttributeValue();
        }
        else if (Enum.class.isInstance(newValue))
        {
            asString = ((Enum<?>) newValue).name();
        }
        else
        {
            asString = newValue.toString();
        }

        super.setValue(asString);
    }
}
