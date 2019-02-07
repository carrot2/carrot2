
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

package org.carrot2.util.attribute.constraint;

import org.simpleframework.xml.Root;

@Root(name = "value-hint")
public class ValueHintEnumConstraint extends Constraint
{
    /*
     * 
     */
    ValueHintEnumConstraint()
    {
        // Hide from the public view.
    }

    /*
     * 
     */
    protected boolean isMet(Object value)
    {
        checkAssignableFrom(value, CharSequence.class);

        /*
         * This is a hint-constraint, so we always return true.
         */
        return true;
    }

    /*
     * 
     */
    @Override
    public String toString()
    {
        return "value-hint";
    }
}
