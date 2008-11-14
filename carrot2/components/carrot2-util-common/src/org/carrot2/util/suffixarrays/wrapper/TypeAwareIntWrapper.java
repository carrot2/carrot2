
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.suffixarrays.wrapper;

/**
 * An integer wrapper that has access to information on tokens' types and can
 * thus provide some additional information, such as whether the token
 * corresponding to an integer code is a stop word.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface TypeAwareIntWrapper extends IntWrapper
{
    /**
     * Checs if the token with given code is a stop word.
     * 
     * @return true if token corresponding to the given code is a stop word
     * @throws NullPointerException when token with the given code does not
     *             exist
     */
    public boolean isStopWord(int code);
}