
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.trc.carrot.tokenizer;

/**
 * Tokenizer that translate common HTML entities to its text form
 */
public class HTMLAwareTokenizer extends DefaultTokenizer {

    HTMLEntityResolver resolver;

    public HTMLAwareTokenizer(HTMLEntityResolver resolver) {
        super();
        this.resolver = resolver;
    }
    
    public HTMLAwareTokenizer(String text, HTMLEntityResolver htmlEntityResolver) {
        super(text);
        resolver = htmlEntityResolver;
    }

    public String nextToken() {
        int type = lastType[0];
        String nextToken = lastToken;
        lastToken = tokenizer.getNextToken(lastType);
        switch(type) {
            case Tokenizer.TYPE_PHRASEMARKER :
                return PHRASE_DELIMITER;
            case Tokenizer.TYPE_SENTENCEMARKER :
                return SENTENCE_DELIMITER;
            case Tokenizer.TYPE_HTMLENTITY:
                return resolver.resolve(nextToken);
            default :
                return nextToken;
        }

    }
}
