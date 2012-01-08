
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

import java.lang.annotation.Annotation;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Checks whether values meet the provided constraints.
 */
public class ConstraintValidator
{
    /**
     * Checks whether the <code>value</code> meets the constraints defined by the
     * provided <code>constraintAnnotations</code>.
     * 
     * @param value the value to be checked
     * @param constraintAnnotations constraint annotations defining the constraints to be
     *            checked.
     * @return an array of constraint annotations which the value does not meet. If all
     *         constraints are met, the returned array is empty.
     */
    public static Annotation [] isMet(Object value, Annotation... constraintAnnotations)
    {
        final List<Constraint> constraints = ConstraintFactory
            .createConstraints(constraintAnnotations);
        final List<Annotation> unmetConstraints = Lists.newArrayList();

        for (Constraint constraint : constraints)
        {
            if (!constraint.isMet(value))
            {
                unmetConstraints.add(constraint.annotation);
            }
        }

        return unmetConstraints.toArray(new Annotation [unmetConstraints.size()]);
    }
}
