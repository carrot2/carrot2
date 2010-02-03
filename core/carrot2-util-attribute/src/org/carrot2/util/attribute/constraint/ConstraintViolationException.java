
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
import java.util.List;

import org.carrot2.util.ListUtils;
import org.carrot2.util.attribute.Attribute;

/**
 * An exception thrown when an attempt is made to bind attribute values that do not meet
 * the constraints.
 */
@SuppressWarnings("serial")
public class ConstraintViolationException extends RuntimeException
{
    /**
     * Value that does not meet the constraints.
     */
    public final Object offendingValue;

    /**
     * Constraints that were not met by the {@link #offendingValue}.
     */
    public final Annotation [] annotations;

    /**
     * Key of the involved {@link Attribute} as defined in {@link Attribute#key()}.
     */
    public final String key;

    /**
     * Creates a new {@link ConstraintViolationException}.
     */
    public ConstraintViolationException(String key, Object offendngValue,
        Annotation... annotations)
    {
        this.offendingValue = offendngValue;
        this.annotations = annotations;
        this.key = key;
    }

    @Override
    public String getMessage()
    {
        final StringBuilder constraintsString = new StringBuilder();
        final List<Constraint> constraints = ListUtils.asArrayList(ConstraintFactory
            .createConstraints(annotations));
        for (int i = 0; i < constraints.size(); i++)
        {
            constraintsString.append("@");
            constraintsString.append(annotations[i].annotationType().getSimpleName());
            constraintsString.append(": ");
            constraintsString.append(constraints.get(i).toString());
            if (i != annotations.length - 1)
            {
                constraintsString.append(", ");
            }
        }

        return "Value: '" + offendingValue + "' of attribute: '" + key
            + "' violates constraints: " + constraintsString;
    }
}
