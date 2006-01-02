
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
package com.dawidweiss.carrot.util.tokenizer.parser;

import com.dawidweiss.carrot.core.local.linguistic.tokens.TypedToken;

/**
 * An implementation of {@link TypedToken} that wraps
 * a <code>String</code> and allows the parser to
 * repeatedly set the image and type of the wrapped token,
 * reusing the token object.
 * 
 * @see TypedToken
 * @author Dawid Weiss
 * @version $Revision$
 */
public class StringTypedToken implements TypedToken {

    private String image;
    private short  type;
    
    /**
     * Assigns a new image and token type to this instance.
     */
    public void assign(String image, short type) {
        this.image = image;
        this.type = type;
    }
    
	/**
     * Returns the type of this token.
     * 
     * <p>May return random values if not initialized previously.
	 */
	public short getType() {
        return type;
	}

    /**
     * A setter for the type of this token.
     * @param type The new type of this token.
     */
    public void  setType(short type) {
        this.type = type;
    }
    
    /**
     * @return Returns the image temporarily associated with this token.
     */
    public String getImage() {
        return image;
    }
    
    
	/** 
     * Appends the image of this token to a string buffer.
     * 
	 * @see com.dawidweiss.carrot.core.local.linguistic.tokens.Token#appendTo(java.lang.StringBuffer)
	 */
	public void appendTo(StringBuffer buffer) {
        buffer.append( image );
	}
    
    
    /**
     * Two tokens are equal, if their images are equal.
     */
    public boolean equals(Object arg) {
        if (arg instanceof StringTypedToken) {
            return image.equals(((StringTypedToken) arg).image);
        } else { 
            return false;
        }
    }

    /**
     * The hash code of a token is its image's hash code.
     */
    public int hashCode() {
        return image.hashCode();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return image;
    }
}
