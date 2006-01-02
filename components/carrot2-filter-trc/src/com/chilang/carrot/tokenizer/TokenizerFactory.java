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
