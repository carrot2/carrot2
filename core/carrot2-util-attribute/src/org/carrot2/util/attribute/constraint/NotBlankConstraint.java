package org.carrot2.util.attribute.constraint;

import org.apache.commons.lang.StringUtils;
import org.simpleframework.xml.Root;

/**
 * Implementation of the {@link NotBlank}.
 */
@Root(name = "not-blank")
class NotBlankConstraint extends Constraint
{
    NotBlankConstraint()
    {
    }

    protected boolean isMet(Object value)
    {
        if (value == null)
        {
            return false;
        }

        checkAssignableFrom(CharSequence.class, value);

        return StringUtils.isNotBlank(value.toString());
    }

    @Override
    public String toString()
    {
        return "not-blank";
    }
}
