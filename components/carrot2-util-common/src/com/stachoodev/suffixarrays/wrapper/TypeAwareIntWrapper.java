/*
 * TypeAwareIntWrapper.java Created on 2004-06-19
 */
package com.stachoodev.suffixarrays.wrapper;

/**
 * An integer wrapper that has access to information on tokens' types and can
 * thus provide some additional information, such as whether the token
 * corresponding to an integer code is a stop word.
 * 
 * @author stachoo
 */
public interface TypeAwareIntWrapper extends IntWrapper
{
    /**
     * Checs if the token with given code is a stop word.
     * 
     * @param token integer code
     * @return true if token corresponding to the given code is a stop word
     * @throws NullPointerException when token with the given code does not
     *             exist
     */
    public boolean isStopWord(int code);
}