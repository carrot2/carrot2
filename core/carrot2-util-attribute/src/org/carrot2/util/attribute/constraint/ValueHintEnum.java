package org.carrot2.util.attribute.constraint;

import java.lang.annotation.*;

/**
 * A set of values for an attribute of type {@link String}. This can be either a hint
 * (for user interfaces) or a restriction (which causes the attribute to behave much like
 * an enum type).
 * <p>
 * By default the constraint accepts values returned from {@link Enum#name()}. If the
 * enum type returned from {@link #values()} implements {@link ValueHintMapping}
 * interface, values checked are retrieved from the
 * {@link ValueHintMapping#getAttributeValue()} method of each individual constant.
 * 
 * @see NotBlank
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = ValueHintEnumConstraint.class)
public @interface ValueHintEnum
{
    Class<? extends Enum<?>> values();

    boolean strict() default false;
}
