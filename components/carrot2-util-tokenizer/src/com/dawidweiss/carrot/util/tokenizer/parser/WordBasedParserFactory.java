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

import com.dawidweiss.carrot.util.tokenizer.parser.javacc.*;
import com.dawidweiss.carrot.util.tokenizer.parser.jflex.*;

/**
 * Returns instances of word based parsers. Use concrete factories
 * {@link #JavaCC}and {@link #JFlex}to obtain parsers based on code generated
 * by JavaCC an JFlex, respectively. The {@link #Default}factory returns
 * JFlex-based parsers as they are much faster.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class WordBasedParserFactory
{
    /** JavaCC-generated parser. Slow not recommended */
    public static final WordBasedParserFactory JavaCC = new WordBasedParserFactory().new JavaCCWordBasedParserFactory();

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

    /**
     * @return
     */
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
    private class JavaCCWordBasedParserFactory extends WordBasedParserFactory
    {
        public JavaCCWordBasedParserFactory()
        {
            parserPool = new SoftReferenceObjectPool(
                new BasePoolableObjectFactory()
                {
                    public Object makeObject() throws Exception
                    {
                        return new JavaCCWordBasedParser();
                    }
                });
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