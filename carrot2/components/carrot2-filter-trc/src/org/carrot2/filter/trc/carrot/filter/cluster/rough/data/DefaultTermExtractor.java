
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.trc.carrot.filter.cluster.rough.data;

import org.carrot2.filter.trc.carrot.filter.cluster.rough.Snippet;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.filter.DefaultTermFilter;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.filter.TermFilter;
import org.carrot2.filter.trc.carrot.tokenizer.ITokenizer;
import org.carrot2.filter.trc.carrot.tokenizer.TokenizerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * Default term extractor that accepts all terms (no stemming, no stop-word removal)
 */
public class DefaultTermExtractor implements TermExtractor {



    TermFilter filter;
    public DefaultTermExtractor() {
        filter = new DefaultTermFilter();
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
            String stem = lower;
            Term term;

            if (filter.accept(stem)) {
                if (stringToTermMap.containsKey(stem)) {
                    term = (Term)stringToTermMap.get(stem);
                } else {
                    term = new SimpleTerm(token, stem, false);
                    stringToTermMap.put(lower, term);
                }
                term.increaseTf(docId);
            }
        }
    }
}
