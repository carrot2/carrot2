
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.trc.carrot.filter.cluster.rough.data;

import org.carrot2.filter.trc.carrot.filter.cluster.rough.filter.StopWordFilter;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.filter.stemmer.Stemmer;

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
