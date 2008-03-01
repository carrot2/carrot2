/**
 *
 */
package org.carrot2.util.attribute.constraint;

import java.lang.annotation.Annotation;

@SuppressWarnings("serial")
public class ConstraintViolationException extends RuntimeException
{
    public final Object offendingValue;
    public final Annotation [] annotations;
    public final String key;

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
        return "Value: " + offendingValue + " of attribute: " + key
            + " violates constraints: " + annotations;
    }
}
