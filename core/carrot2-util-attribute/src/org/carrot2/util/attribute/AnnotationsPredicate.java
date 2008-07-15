package org.carrot2.util.attribute;

import java.lang.annotation.Annotation;

import com.google.common.base.Predicate;

/**
 * A predicate that tests the presence of a given set of annotations on
 * an {@link AttributeDescriptor}.
 */
public final class AnnotationsPredicate implements Predicate<AttributeDescriptor>
{
    private final Class<? extends Annotation> [] annotationClasses;
    private final boolean requireAll;

    public AnnotationsPredicate(boolean requireAll, Class<? extends Annotation>... annotationClasses)
    {
        this.annotationClasses = annotationClasses;
        this.requireAll = requireAll;
    }

    public boolean apply(AttributeDescriptor descriptor)
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