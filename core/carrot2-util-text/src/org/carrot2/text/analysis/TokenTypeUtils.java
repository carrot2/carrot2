
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

/**
 * Utility methods for working with {@link ITokenTypeAttribute}s.
 */
public final class TokenTypeUtils
{
    private TokenTypeUtils()
    {
        // no instances.
    }

    /**
     * Mask the given raw token type and leave just the token
     * type bits.
     */
    public static int maskType(int rawType) 
    {
        return rawType & ITokenTypeAttribute.TYPE_MASK;
    }

    /**
     * Returns <code>true</code> if the given type has {@link ITokenTypeAttribute#TF_SEPARATOR_DOCUMENT} set.
     */
    public static boolean isDocumentSeparator(int type)
    {
        return (type & ITokenTypeAttribute.TF_SEPARATOR_DOCUMENT) != 0;
    }
}
