package org.carrot2.util.attribute;

import org.apache.commons.lang.StringUtils;

public enum AttributeLevel
{
    BASIC,

    MEDIUM,

    ADVANCED;

    @Override
    public String toString()
    {
        return StringUtils.capitalize(name().toLowerCase());
    }
}
