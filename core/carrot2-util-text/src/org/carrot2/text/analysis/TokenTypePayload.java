
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

import org.apache.lucene.index.Payload;

/**
 * Lucene's {@link Payload} implementing {@link ITokenType}.
 */
final class TokenTypePayload extends Payload implements ITokenType
{
    private static final long serialVersionUID = 0x200804101135L;

    /**
     * Flags associated with the current token.
     */
    private int flags;

    /**
     * @see ITokenType#getRawFlags()
     */
    public int getRawFlags()
    {
        return flags;
    }

    /**
     * Sets {@link #flags}.
     */
    void setRawFlags(int flags)
    {
        this.flags = flags;
    }

    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof TokenTypePayload))
        {
            return false;
        }

        return super.equals(object) && ((TokenTypePayload) object).flags == this.flags;
    }

    @Override
    public int hashCode()
    {
        return super.hashCode() % flags;
    }
}
