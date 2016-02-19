
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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import javax.xml.crypto.NoSuchMechanismException;

import org.carrot2.workbench.editors.AttributeEditorInfo;


/**
 * An attribute editor for objects whose classes declare a conversion-from-string
 * (<tt>valueOf(String)</tt>) method.
 */
public class ClassWithValueOfEditor extends StringEditor
{
    private Method valueOf;

    @Override
    protected AttributeEditorInfo init(Map<String, Object> defaultValues)
    {
        Class<?> clazz = super.descriptor.type;
        if (clazz == null)
            throw new IllegalArgumentException("Attribute's class must exist: "
                + super.getAttributeKey());
        try
        {
            Method valueOf = clazz.getDeclaredMethod("valueOf", String.class);
            if (!Modifier.isStatic(valueOf.getModifiers()))
                throw new NoSuchMethodException();
            if (!Modifier.isPublic(valueOf.getModifiers()))
                throw new NoSuchMechanismException();
            this.valueOf = valueOf;
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException("Attribute's class must have a public static valueOf method: "
                + super.getAttributeKey() + ", class: " + clazz.getName());
        }

        return super.init(defaultValues);
    }

    @Override
    protected boolean isValid(String newValue)
    {
        try {
            valueOf.invoke(null, newValue);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
