/*
 * WordBasedParserFactory.java Created on 2004-06-17
 */
package com.dawidweiss.carrot.util.tokenizer.parser;

import org.apache.commons.pool.*;
import org.apache.commons.pool.impl.*;

/**
 * @author stachoo
 */
public class WordBasedParserFactory
{
    /** Parser pool */
    private static ObjectPool parserPool;

    /** No public constructor */
    private WordBasedParserFactory()
    {
        // No public constructor
    }
    
    /** Initialize the parser pool */
    static {
        parserPool = new SoftReferenceObjectPool(
                new BasePoolableObjectFactory()
                {
                    public Object makeObject() throws Exception
                    {
                        return new WordBasedParser();
                    }
                });
    }

    /**
     * @return
     */
    public static WordBasedParser borrowParser()
    {
        try
        {
            return (WordBasedParser) parserPool.borrowObject();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Cannot borrow a parser", e);
        }
    }
    
    /**
     * @param parser
     */
    public static void returnParser(WordBasedParser parser)
    {
        try
        {
            parserPool.returnObject(parser);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Cannot return a parser", e);
        }
    }
}