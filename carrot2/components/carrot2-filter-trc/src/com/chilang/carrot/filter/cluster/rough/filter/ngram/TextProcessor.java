/**
 * 
 * @author chilang
 * Created 2003-09-11, 01:01:00.
 */
package com.chilang.carrot.filter.cluster.rough.filter.ngram;

import com.chilang.carrot.tokenizer.ITokenizer;



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
