/**
 * 
 * @author chilang
 * Created 2003-08-22, 22:02:15.
 */
package com.chilang.carrot.tokenizer;

public class TokenizerFactory {

    public static final int HTML = 1;

    private TokenizerFactory() {};

    public static ITokenizer getTokenizer() {
        return new HTMLAwareTokenizer(new CommonEntityResolver());
    }

    public static ITokenizer getTokenizer(int type) {
        switch(type) {
            case HTML :
                return new HTMLAwareTokenizer(new CommonEntityResolver());
            default :
                return new DefaultTokenizer();
        }
    }
}
