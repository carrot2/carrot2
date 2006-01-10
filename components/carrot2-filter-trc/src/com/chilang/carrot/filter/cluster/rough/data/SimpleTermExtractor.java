
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

package com.chilang.carrot.filter.cluster.rough.data;

import com.chilang.carrot.filter.cluster.rough.Snippet;
import com.chilang.carrot.filter.cluster.rough.filter.TermFilter;
import com.chilang.carrot.filter.cluster.rough.filter.stemmer.Stemmer;
import com.chilang.carrot.tokenizer.ITokenizer;
import com.chilang.carrot.tokenizer.TokenizerFactory;

import java.util.*;



public class SimpleTermExtractor implements TermExtractor {


    private Stemmer stemmer;
    private TermFilter filter;


    public SimpleTermExtractor(Stemmer stemmer, TermFilter filter) {
        this.stemmer = stemmer;
        this.filter = filter;
    }


    /**
     * Extract term from snippet
     * @param document snippet
     * @return Collection of term
     */
    public Object extractFromSnippet(Document document) {
        Map stringToTermMap = new HashMap();
        ITokenizer tokenizer = TokenizerFactory.getTokenizer();
        tokenizer.restartTokenizer(((Snippet)document).getTitle());
        extractTerms(((Snippet)document).getId(), tokenizer, stringToTermMap);
        document.setStrongTerms(stringToTermMap.keySet());
        String desc = ((Snippet)document).getDescription();
        if (desc != null && desc.length() > 0) {
            tokenizer.restartTokenizer(desc);
            extractTerms(((Snippet)document).getId(), tokenizer, stringToTermMap);
        }

        return stringToTermMap.values();
    }

    private void extractTerms(String docId, ITokenizer tokenizer, Map stringToTermMap) {
        while (tokenizer.hasToken()) {
            String token = tokenizer.nextToken();
            String lower = token.toLowerCase();
            String stem = stemmer.stem(lower);
            Term term;



            if (stringToTermMap.containsKey(stem)) {
                term = (Term)stringToTermMap.get(stem);
                term.increaseTf(docId);
            } else {
                //check if word is already a stop word
                if (filter.accept(lower)
                        && filter.accept(stem)) {
//                    System.out.println("["+token +" -> "+stem+"]");
                    term = new SimpleTerm(token, stem, false);

                    //filter out all stop word
                    stringToTermMap.put(lower, term);
                    term.increaseTf(docId);
                }
            }
        }
    }
}
