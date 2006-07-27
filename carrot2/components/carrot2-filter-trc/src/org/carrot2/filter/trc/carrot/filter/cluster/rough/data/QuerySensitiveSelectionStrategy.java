
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

import org.carrot2.filter.trc.util.StringUtils;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.filter.stemmer.Stemmer;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.CommonFactory;

import java.util.Set;
import java.util.HashSet;


public class QuerySensitiveSelectionStrategy extends DocumentFrequencySelectionStrategy {


    /**
     * Set of stemmed query words
     */
    Set queryWords;

    /**
     * Construct selection strategy sensitive to query words and document frequency
     * @param documentFrequency minimum document frequency
     * @param query query string
     */
    public QuerySensitiveSelectionStrategy(int documentFrequency, String query) {
        super(documentFrequency);
        queryWords = createStemmedQueryWords(query);
    }

    private static Set createStemmedQueryWords(String query) {

        Set stemmed = new HashSet();
        String[] words = splitQueryIntoWords(query);

        Stemmer stemmer = CommonFactory.createDefaultStemmer();
        for (int i = 0; i < words.length; i++) {
            stemmed.add(stemmer.stem(words[i]));
        }
        return stemmed;
    }

    private static String[] splitQueryIntoWords(String query) {
        return StringUtils.trim(query, '\"').trim().toLowerCase().split(" ");
    }

    public boolean accept(Term term) {
        return super.accept(term) && isNotQueryWord(term);
    }

    private boolean isNotQueryWord(Term term) {
        return !queryWords.contains(term.getStemmedTerm());
    }
}
