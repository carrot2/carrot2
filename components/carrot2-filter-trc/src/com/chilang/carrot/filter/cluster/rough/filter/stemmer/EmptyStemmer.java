/**
 * 
 * @author chilang
 * Created 2003-12-08, 02:24:05.
 */
package com.chilang.carrot.filter.cluster.rough.filter.stemmer;

public class EmptyStemmer implements Stemmer {
    public String stem(String word) {
        return word;
    }
}
