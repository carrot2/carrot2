/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.util.tokenizer.parser;

import org.apache.commons.pool.*;
import org.apache.commons.pool.impl.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
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