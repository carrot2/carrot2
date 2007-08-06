
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

package org.carrot2.filter.trc.carrot.filter.cluster.rough.filter.ngram;

import org.carrot2.filter.trc.carrot.tokenizer.ITokenizer;



/**
 * Interface for processor of stream of text token
 */
public interface TextProcessor {

    /**
     * Process tokens from given tokenizer
     * @param tokenizer
     */
    public void process(ITokenizer tokenizer);
    
}
