
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

package org.carrot2.filter.trc.carrot.tokenizer;

import org.carrot2.filter.trc.carrot.tokenizer.ITokenizer;

import java.util.Collection;
import java.util.ArrayList;

public class DefaultTokenizer implements ITokenizer{

    Tokenizer tokenizer;

    String lastToken;
    int[] lastType;

    public DefaultTokenizer() {
        tokenizer = Tokenizer.getTokenizer();
    }
    
    public DefaultTokenizer(String text) {
        this();
        restartTokenizer(text);
    }
    public boolean hasToken() {
        if (lastToken == null)
            return false;
        return true;
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
            default :
                return nextToken;
        }
    }

    public String[] tokenize() {
        Collection arr = new ArrayList();
        while (this.hasToken()) {
            arr.add(this.nextToken());
        }
        return (String[])arr.toArray(new String[0]);

    }

    public void restartTokenizer(String text) {
        tokenizer.restartTokenizerOn(text);
        lastType = new int[1];
        //process one token ahead
        lastToken = tokenizer.getNextToken(lastType);
    }
}
