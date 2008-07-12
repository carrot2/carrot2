package org.carrot2.util.attribute.constraint;

import org.apache.commons.lang.StringUtils;

/**
 * Implementation of the {@link NotBlank}.
 */
class NotBlankConstraint extends Constraint
{
    NotBlankConstraint()
    {
    }

    boolean isMet(Object value)
    {
        if (value == null)
        {
            return false;
        }

        if (value instanceof CharSequence)
        {
            return StringUtils.isNotBlank(value.toString());
        }

        throw new RuntimeException("Expected an instance of CharSequence: "
            + value);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj instanceof NotBlankConstraint)
        {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return 0;
    }

    @Override
    public String toString()
    {
        return "not-blank";
    }
}
