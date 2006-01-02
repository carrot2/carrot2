
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.core.local.linguistic.tokens;

/**
 * A token with an associated image of its conflated form (a stem, lexeme or
 * any other symbol that is unique for the meaning-related family of inflected
 * forms of this word).
 * 
 * <p>
 * For more information, see {@link
 * com.dawidweiss.carrot.core.local.linguistic.Stemmer} interface.
 * </p>
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public interface StemmedToken extends Token {
    /**
     * @return Returns the conflated representation of this token. May return
     *         <code>null</code>, in such case the token has no associated
     *         conflated form.
     */
    public String getStem();
}
