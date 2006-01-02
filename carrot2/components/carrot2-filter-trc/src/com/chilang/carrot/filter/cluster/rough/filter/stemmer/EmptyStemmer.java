package com.chilang.carrot.filter.cluster.rough.filter.stemmer;

public class EmptyStemmer implements Stemmer {
    public String stem(String word) {
        return word;
    }
}
