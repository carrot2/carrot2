/**
 * 
 */
package org.carrot2.core.attribute;

import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.Annotation;

/**
 *
 */
public final class JavaDocBuilderUtils
{
    private JavaDocBuilderUtils()
    {
        // No instantiation
    }

    public static boolean hasAnnotation(AbstractJavaEntity javaEntity,
        Class<?> requestedAnnotationClass)
    {
        return getAnnotation(javaEntity, requestedAnnotationClass) != null;
    }

    public static Annotation getAnnotation(AbstractJavaEntity javaEntity,
        Class<?> requestedAnnotationClass)
    {
        for (Annotation annotation : javaEntity.getAnnotations())
        {
            if (requestedAnnotationClass.getName()
                .equals(annotation.getType().getValue()))
            {
                return annotation;
            }
        }
        return null;
    }

    public static String normalizeSpaces(String string)
    {
        if (string == null)
        {
            return null;
        }
        return string.replaceAll("[\\t\\r\\n]+", " ");
    }
}
