/**
 * 
 * @author chilang
 * Created 2003-08-22, 01:58:01.
 */
package com.chilang.carrot.tokenizer;

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
