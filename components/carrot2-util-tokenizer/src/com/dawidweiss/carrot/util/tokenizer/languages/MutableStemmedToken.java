/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.util.tokenizer.languages;

import com.dawidweiss.carrot.core.local.linguistic.tokens.StemmedToken;
import com.dawidweiss.carrot.util.tokenizer.parser.StringTypedToken;


/**
 * A mutable stemmed token.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class MutableStemmedToken extends StringTypedToken 
	implements StemmedToken {

    private String stem;
    
    /**
     * Sets the stem associated with this token.
     * @param stem
     */
    public void setStem(String stem) {
        this.stem = stem;
    }

    /** 
     * @return Returns the stem associated with this token, or <code>null</code>
     * @see com.dawidweiss.carrot.core.local.linguistic.tokens.StemmedToken#getStem()
     */
    public String getStem() {
        return stem;
    }
    
    /**
     * Override assign method to clear the internal <code>stem</code> field.
     * @see com.dawidweiss.carrot.util.tokenizer.parser.StringTypedToken#assign(java.lang.String, short)
     */
    public void assign(String image, short type) {
        super.assign(image, type);
        this.stem = null;
    }
}
