/**
 * 
 * @author chilang
 * Created 2003-07-22, 18:03:32.
 */
package com.chilang.carrot.filter.cluster.rough.data;

import com.chilang.carrot.filter.cluster.rough.Snippet;
import com.chilang.carrot.filter.cluster.rough.filter.DefaultTermFilter;
import com.chilang.carrot.filter.cluster.rough.filter.TermFilter;
import com.chilang.carrot.tokenizer.ITokenizer;
import com.chilang.carrot.tokenizer.TokenizerFactory;

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
