

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.filter.stemming;


import java.io.IOException;
import java.io.Reader;
import java.util.StringTokenizer;


/**
 * This a stemming servlet for Carrot2 search results clustering application.
 */
public class StopWordsSet
    extends java.util.HashSet
{
    public StopWordsSet()
    {
        super();
    }


    public StopWordsSet(Reader stopWordsFile)
        throws IOException
    {
        StringTokenizer tokenizer = new StringTokenizer(
                new String(org.put.util.io.FileHelper.readFullyAndCloseInput(stopWordsFile))
            );

        while (tokenizer.hasMoreTokens())
        {
            this.add(tokenizer.nextToken());
        }
    }
}
