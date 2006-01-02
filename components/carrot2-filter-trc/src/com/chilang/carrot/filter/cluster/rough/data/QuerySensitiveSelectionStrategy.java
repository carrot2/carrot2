package com.chilang.carrot.filter.cluster.rough.data;

import com.chilang.util.StringUtils;
import com.chilang.carrot.filter.cluster.rough.filter.stemmer.Stemmer;
import com.chilang.carrot.filter.cluster.rough.CommonFactory;

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
