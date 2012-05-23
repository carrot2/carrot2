
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

import java.lang.annotation.*;
import java.lang.reflect.Field;

/**
 * Denotes fields whose values should be bound (set or collected) by
 * {@link AttributeBinder}. A field marked with {@link Attribute} will be referred to as
 * <strong>an attribute</strong>. In order for a type to have any of its attributes
 * bound, the type must be annotated with {@link Bindable}. Otherwise, the
 * {@link Attribute} annotations will be ignored.
 * <p>
 * Attributes denoted by {@link Attribute} must also be marked with one or both of
 * {@link Input} or {@link Output}, which define the direction of binding. Attribute
 * fields can also be marked with some extra domain-specific annotations that can then be
 * used to selectively bind only some of a {@link Bindable}'s attributes (see
 * {@link AttributeBinder} for more details).
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Attribute
{
    /**
     * The unique identifier of this attribute. The identifier is used as the key when
     * providing and collecting attribute values though the
     * {@link AttributeBinder#set(Object, java.util.Map, Class...)} method. If the
     * key is not provided, the attribute will have its key composed of the prefix defined
     * by the {@link Bindable} annotation on the enclosing class (see
     * {@link Bindable#prefix()})  followed by a dot (<code>.</code>) and the name of
     * the attribute field as returned by {@link Field#getName()}.
     */
    String key() default "";

    /**
     * If true, inherit attribute documentation from another attribute (field). Used in conjunction with
     * {@link Bindable#inherit()}. The {@link #key()} of this attribute must match exactly one of
     * the inherited attributes.
     *
     * @see Bindable#inherit()
     */
    boolean inherit() default false;
}
