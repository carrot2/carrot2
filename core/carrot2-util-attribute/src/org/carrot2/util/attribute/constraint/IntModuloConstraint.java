
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute.constraint;

import java.lang.annotation.Annotation;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Implementation of the {@link IntModuloConstraint}.
 */
@Root(name = "int-modulo")
class IntModuloConstraint extends Constraint
{
    @Attribute
    int modulo;
    
    @Attribute
    int offset;

    protected boolean isMet(Object value)
    {
        if (value == null)
        {
            return false;
        }

        checkAssignableFrom(Integer.class, value);

        final Integer v = (Integer) value;
        return Math.abs((v % modulo)) == offset;
    }

    @Override
    public String toString()
    {
        return "modulo(modulo = " + modulo + ", offset = " + offset + ")";
    }

    @Override
    public void populateCustom(Annotation annotation)
    {
        final IntModulo modulo = (IntModulo) annotation;
        this.modulo = modulo.modulo();
        this.offset = modulo.offset();
    }
}
