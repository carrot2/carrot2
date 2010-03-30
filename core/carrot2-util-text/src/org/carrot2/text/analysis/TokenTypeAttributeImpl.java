/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.analysis;

import java.io.Serializable;

import org.apache.lucene.util.AttributeImpl;

/**
 * Lucene's attribute implementing {@link ITokenTypeAttribute}.
 */
public final class TokenTypeAttributeImpl extends AttributeImpl implements ITokenTypeAttribute,
    Cloneable, Serializable
{
    private static final long serialVersionUID = 0x201003301033L;

    /**
     * Flags associated with the current token.
     */
    private int flags;

    /**
     * @see ITokenTypeAttribute#getRawFlags()
     */
    public int getRawFlags()
    {
        return flags;
    }

    /**
     * Sets {@link #flags}.
     */
    public void setRawFlags(int flags)
    {
        this.flags = flags;
    }

    /**
     *
     */
    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof TokenTypeAttributeImpl))
        {
            return false;
        }

        return ((TokenTypeAttributeImpl) object).flags == this.flags;
    }

    /**
     * 
     */
    @Override
    public int hashCode()
    {
        throw new UnsupportedOperationException(
            "Hash code of this object may change over time: " + getClass());
    }

    @Override
    public void clear()
    {
        this.flags = 0;
    }

    @Override
    public void copyTo(AttributeImpl target)
    {
        ((ITokenTypeAttribute) target).setRawFlags(this.flags);
    }
}
