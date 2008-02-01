/**
 * 
 */
package org.carrot2.core.attribute.metadata;

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
        for (Annotation annotation : javaEntity.getAnnotations())
        {
            if (requestedAnnotationClass.getName()
                .equals(annotation.getType().getValue()))
            {
                return true;
            }
        }
        return false;
    }
}
