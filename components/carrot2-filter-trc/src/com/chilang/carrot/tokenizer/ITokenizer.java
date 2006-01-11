
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

package com.chilang.carrot.tokenizer;

public interface ITokenizer {

    public static final String SENTENCE_DELIMITER = ".";
    public static final String PHRASE_DELIMITER = ",";
    /**
     * Check if any token is left
     */
    public boolean hasToken();
    /**
     * Return next token and advance the processing index
     */
    public String nextToken();
    /**
     * Tokenize all text (starting from current processing index) into array of String.
     * This is equivalent to exhaustively calling hasToken, nextToken until hasToken return false. 
     */
    public String[] tokenize();

    public void restartTokenizer(String text);
    
}
