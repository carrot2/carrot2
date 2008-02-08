/**
 * 
 */
package org.carrot2.core.attribute;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.Annotation;

/**
 *
 */
public final class JavaDocBuilderUtils
{
    private static final Pattern FIRST_SENTENCE_PATTERN = Pattern
        .compile("\\.(?<!((\\w\\.){2,5}+))(\\s|\\z)");

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

    public static int getEndOfFirstSenteceCharIndex(String text)
    {
        Matcher matcher = FIRST_SENTENCE_PATTERN.matcher(text);
        if (matcher.find())
        {
            return matcher.start();
        }
        else
        {
            return -1;
        }
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
