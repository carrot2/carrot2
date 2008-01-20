
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

package org.carrot2.core.linguistic.tokens;

/**
 * A token is an atomic entity discovered from raw text. If you need an
 * image of a Token, a preferable method is to acquire a copy of the
 * token using {@link #appendTo(StringBuffer)} method. 
 * 
 * <p>
 * As it turned out (sadly), token-image access methods are used so frequently
 * that it is very inconvenient not to have them. Therefore a new method
 * {@link #getImage()} is added to this interface. Having said that, whenever possible,
 * {@link #appendTo(StringBuffer)} method should be favored over the direct
 * image access. 
 * </p>
 * 
 * <p>
 * <b>Tokens must fulfill equivalence relationship ({@link
 * Object#equals(Object)} and  {@link Object#hashCode()} methods).</b>
 * </p>
 */
public interface Token {
    /**
     * Appends the image of this token to a {@link StringBuffer} instance.
     *
     * @param buffer The {@link StringBuffer} instance to append the image of
     *        this token to.
     */
    public void appendTo(StringBuffer buffer);
    
    /**
     * @return Returns an image of this token as a {@link String} object.
     */
    public String getImage();
}
