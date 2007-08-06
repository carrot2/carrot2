
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.tokenizer.languages;

import org.carrot2.core.linguistic.tokens.StemmedToken;
import org.carrot2.util.tokenizer.parser.StringTypedToken;


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
     * @see org.carrot2.core.linguistic.tokens.StemmedToken#getStem()
     */
    public String getStem() {
        return stem;
    }
    
    /**
     * Override assign method to clear the internal <code>stem</code> field.
     * @see org.carrot2.util.tokenizer.parser.StringTypedToken#assign(java.lang.String, short)
     */
    public void assign(String image, short type) {
        super.assign(image, type);
        this.stem = null;
    }
}
