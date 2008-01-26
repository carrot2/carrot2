/**
 * 
 */
package org.carrot2.core.attribute.javadoc;

import java.io.Closeable;
import java.io.IOException;

import org.carrot2.core.attribute.AttributeNames;

import com.sun.javadoc.*;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;

/**
 *
 */
public class AttributeJavadocUtils
{
    static final String BINDABLE_ANNOTATION_NAME = "Bindable";
    
    static final String ATTRIBUTE_ANNOTATION_NAME = "Attribute";
    static final String ATTRIBUTE_ANNOTATION_KEY_NAME = "key";
    
    static final String ATTRIBUTE_NAMES_CLASS_NAME = "org.carrot2.core.attribute.AttributeNames";

    /**
     * Looks up the field of {@link AttributeNames} whose value corresponds to the
     * provided <code>key</code>.
     */
    static Doc getAttributeNameConstantFieldDoc(RootDoc rootDoc,
        AnnotationDesc attributeAnnotation)
    {
        ElementValuePair [] elementValues = attributeAnnotation.elementValues();
        for (ElementValuePair elementValuePair : elementValues)
        {
            if (AttributeJavadocUtils.ATTRIBUTE_ANNOTATION_KEY_NAME
                .equals(elementValuePair.element().name()))
            {
                final String key = elementValuePair.value().toString().replaceAll("\"",
                    "");

                ClassDoc classDoc = rootDoc
                    .classNamed(AttributeJavadocUtils.ATTRIBUTE_NAMES_CLASS_NAME);
                if (classDoc == null)
                {
                    return null;
                }

                for (FieldDoc fieldDoc : classDoc.fields(false))
                {
                    if (key.equals(fieldDoc.constantValue()))
                    {
                        return fieldDoc;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Copied from CloseableUtils to avoid pulling carrot2-util-common dependencies.
     */
    static void closeIgnoringException(Closeable closeable)
    {
        if (closeable != null)
        {
            try
            {
                closeable.close();
            }
            catch (IOException e)
            {
                // Ignore.
            }
        }
    }

    /**
     * Determines whether there is a specific annotation in the provided array.
     */
    static AnnotationDesc getAnnotation(final AnnotationDesc [] annotations,
        String annotationName)
    {
        for (final AnnotationDesc annotationDesc : annotations)
        {
            if (annotationName.equals(annotationDesc.annotationType().name()))
            {
                return annotationDesc;
            }
        }

        return null;
    }

    static boolean isBindable(ClassDoc classDoc, RootDoc rootDoc)
    {
        final AnnotationDesc [] annotations = classDoc.annotations();
        return getAnnotation(annotations, AttributeJavadocUtils.BINDABLE_ANNOTATION_NAME) != null;
    }
}
