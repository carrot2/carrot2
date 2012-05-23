
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

/**
 * Denotes types that will have some of their fields bound (set or collected) by the
 * {@link AttributeBinder}. Fields to be bound are denoted by the {@link Attribute}
 * annotation. If a type has some its fields annotated by {@link Attribute}, but the type
 * itself is not marked with {@link Bindable}, the {@link Attribute} annotations will be
 * ignored.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Bindable
{
    /**
     * The prefix for the keys of attributes defined in the bindable type. If the prefix
     * is not provided, the fully qualified class name (as obtained from
     * {@link Class#getName()} will be used. For more information on how
     * {@link Bindable#prefix()} works with attribute keys, see {@link Attribute#key()};
     */
    String prefix() default "";

    /**
     * Inherit attribute descriptions (metadata) from other bindable types. Each attribute
     * should use <code>inherit</code> key to indicate which attribute it inherits 
     * metadata from.
     */
    Class<?>[] inherit() default {};
}
