
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

package org.carrot2.util.attribute;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

/**
 * A predicate that tests the presence of a given set of annotations on
 * an {@link AttributeDescriptor}.
 */
public final class AnnotationsPredicate implements Predicate<AttributeDescriptor>
{
    private final Class<? extends Annotation> [] annotationClasses;
    private final boolean requireAll;

    @SafeVarargs
    public AnnotationsPredicate(boolean requireAll, Class<? extends Annotation>... annotationClasses)
    {
        this.annotationClasses = annotationClasses;
        this.requireAll = requireAll;
    }

    public boolean test(AttributeDescriptor descriptor)
    {
        for (final Class<? extends Annotation> annotationClass : annotationClasses)
        {
            if (descriptor.getAnnotation(annotationClass) == null ^ !requireAll)
            {
                return !requireAll;
            }
        }

        return requireAll;
    }
}
