/**
 * 
 * @author chilang
 * Created 2003-09-11, 01:09:55.
 */
package com.chilang.carrot.filter.cluster.rough.filter.ngram;

import com.chilang.carrot.filter.cluster.rough.Snippet;


/**
 * Interface for general processor of snippet
 */
public interface SnipperProcessor {

    /**
     * Do some processing with given snippet
     * @param snippet
     */
    public void process(Snippet snippet);

    
}
