
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

package com.dawidweiss.carrot.util.tokenizer.parser;

import org.apache.commons.pool.*;
import org.apache.commons.pool.impl.*;

import com.dawidweiss.carrot.util.tokenizer.parser.jflex.*;

/**
 * Returns instances of word based parsers. Use concrete factory
 * ({@link #JFlex}) to obtain parsers.
 * 
 * The {@link #Default} factory returns JFlex-based parsers as
 * they are much faster.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class WordBasedParserFactory
{
    /** JFlex-generated parser. Fast and recommended */
    public static final WordBasedParserFactory JFlex = new WordBasedParserFactory().new JFlexWordBasedParserFactory();

    /** Default factory: JFlex */
    public static final WordBasedParserFactory Default = JFlex;

    /** Parser pool */
    protected ObjectPool parserPool;

    /** No public constructor */
    private WordBasedParserFactory()
    {
        // No public constructor
    }

    public WordBasedParserBase borrowParser()
    {
        try
        {
            return (WordBasedParserBase) parserPool.borrowObject();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Cannot borrow a parser", e);
        }
    }

    /**
     * @param parser
     */
    public void returnParser(WordBasedParserBase parser)
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

    /**
     * @author Stanislaw Osinski
     * @version $Revision$
     */
    private class JFlexWordBasedParserFactory extends WordBasedParserFactory
    {
        public JFlexWordBasedParserFactory()
        {
            parserPool = new SoftReferenceObjectPool(
                new BasePoolableObjectFactory()
                {
                    public Object makeObject() throws Exception
                    {
                        return new JFlexWordBasedParser();
                    }
                });
        }
    }
}