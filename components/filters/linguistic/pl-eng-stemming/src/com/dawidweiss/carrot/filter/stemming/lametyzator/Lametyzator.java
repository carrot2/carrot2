

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


package com.dawidweiss.carrot.filter.stemming.lametyzator;


import com.dawidweiss.carrot.filter.stemming.DirectStemmer;
import org.put.fsa.FSA;
import org.put.linguistics.stemming.fsa.FSAStemmer;
import java.io.IOException;
import java.io.InputStream;


/**
 *
 */
public class Lametyzator
    implements DirectStemmer
{
    private final transient FSAStemmer stemmer;

    /**
     * Creates a new instance of Lametyzator lemmatization engine for Polish. The FSA automaton is
     * read using classloader's facilities.
     */
    public Lametyzator()
        throws IOException, IllegalArgumentException
    {
        InputStream fsaStream = this.getClass().getResourceAsStream("polski.fsa");

        if (fsaStream == null)
        {
            throw new RuntimeException("Cannot load necessary FSA dictionary 'polski.fsa'");
        }

        FSA fsa = FSA.getInstance(fsaStream, "iso8859-2");
        stemmer = new FSAStemmer(fsa, "iso8859-2", '+');
    }

    /**
     * Stems a term in characters array (a performance consideration) starting at index
     * <code>beginCharacter</code> (inclusive) and ending at <code>endCharacter</code>
     * (exclusive). This follows the pattern of <code>String.substring</code>. If more than one
     * base form the term is available, the first one returned by the FSAStemmer is returned.
     */
    public String getStem(char [] charArray, int beginCharacter, int endCharacter)
    {
        String string = new String(charArray, beginCharacter, endCharacter - beginCharacter)
            .toLowerCase();
        String [] stemmedForms = stemmer.stem(string);

        if (stemmedForms.length > 0)
        {
            return stemmedForms[0];
        }
        else
        {
            return null;
        }
    }
}
