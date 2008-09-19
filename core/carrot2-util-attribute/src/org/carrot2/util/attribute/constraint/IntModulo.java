package org.carrot2.util.attribute.constraint;

import java.lang.annotation.*;


/**
 * Requires that the integer attribute value meets the condition:
 * <code>attributeValue % {@link #modulo()}</code> == {@link #offset()}.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = IntModuloConstraint.class)
public @interface IntModulo
{
    /**
     * Modulo value.
     */
    int modulo();

    /**
     * Offset value.
     */
    int offset() default 0;
}
