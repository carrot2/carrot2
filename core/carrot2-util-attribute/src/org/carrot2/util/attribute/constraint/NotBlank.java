package org.carrot2.util.attribute.constraint;

import java.lang.annotation.*;

import org.apache.commons.lang.StringUtils;

/**
 * Requires that the {@link CharSequence} value is not blank, i.e. is not null and has
 * characters other than white space.
 * 
 * @see StringUtils#isNotBlank(String)
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = NotBlankConstraint.class)
public @interface NotBlank
{
}
