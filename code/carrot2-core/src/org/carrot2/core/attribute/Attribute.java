package org.carrot2.core.attribute;

import java.lang.annotation.*;
import java.lang.reflect.Field;

import org.carrot2.core.ProcessingComponent;
import org.carrot2.core.SimpleController;

/**
 * Denotes fields whose values can be bound (set or read) by {@link AttributeBinder}. In
 * order for a class to have some of its fields bound, the class must be annotated with
 * {@link Bindable}. Fields marked with {@link Attribute} must also be marked with one of
 * {@link Input} or {@link Output} <strong>and</strong> one of {@link Init} or
 * {@link Processing}, which define the direction and time of binding, respectively.
 * <p>
 * {@link AttributeBinder} is extensively used by the Carrot<sup>2</sup> controllers to
 * manage component's attributes. For more details, please see {@link ProcessingComponent}
 * and the implementation of {@link SimpleController}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Attribute
{
    /**
     * The unique identifier of this attribute. This identifier is used as the key when
     * providing and collecting attribute values though the
     * {@link AttributeBinder#bind(Object, java.util.Map, Class, Class)} method. If the
     * key is not provided, the attribute will have a key composed of the prefix defined
     * by the {@link Bindable} annotation on the enclosing class (see
     * {@link Bindable#prefix()}) followed by a dot (<code>.</code>) and the name of
     * the attribute field as returned by {@link Field#getName()}.
     */
    String key() default "";
}
