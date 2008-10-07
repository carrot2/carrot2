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

        if (value instanceof CharSequence)
        {
            return StringUtils.isNotBlank(value.toString());
        }
        else
        {
            return true;
        }
    }

    @Override
    public String toString()
    {
        return "not-blank";
    }
}
