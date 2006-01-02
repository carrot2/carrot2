package com.chilang.carrot.tokenizer;

public interface ITokenizer {

    public static final String SENTENCE_DELIMITER = ".";
    public static final String PHRASE_DELIMITER = ",";
    /**
     * Check if any token is left
     * @return
     */
    public boolean hasToken();
    /**
     * Return next token and advance the processing index
     * @return
     */
    public String nextToken();
    /**
     * Tokenize all text (starting from current processing index) into array of String.
     * This is equivalent to exhaustively calling hasToken, nextToken until hasToken return false. 
     * @return
     */
    public String[] tokenize();

    public void restartTokenizer(String text);
    
}
