package com.chilang.carrot.filter.cluster.rough.data;

import com.chilang.carrot.filter.cluster.rough.filter.StopWordFilter;
import com.chilang.carrot.filter.cluster.rough.filter.stemmer.Stemmer;

public class StemmedTermExtractor implements TermExtractor {


    private Stemmer stemmer;
    private StopWordFilter filter;
//    private AbstractTermFactory termFactory;
    StemmedTermExtractor(Stemmer stemmer, StopWordFilter filter) {
        this.stemmer = stemmer;
        this.filter = filter;
//        this.termFactory = new StemmedTermFactory(stemmer, filter);
    }

    private String filterWebSpecific(String word) {
        return word.replaceAll("&quot", "").toLowerCase();
    }

    private boolean isAllDigits(String token) {
        boolean isDigit = true;
        for (int i=0; i<token.length() && isDigit; i++) {
            isDigit = Character.isDigit(token.charAt(i));
        }
        return isDigit;
    }

    public Object extractFromSnippet(Document document) {
        return null;
    }
}
