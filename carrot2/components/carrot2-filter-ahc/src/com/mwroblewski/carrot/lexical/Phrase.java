

/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.mwroblewski.carrot.lexical;


import java.util.StringTokenizer;
import java.util.Vector;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;


/**
 * @author Michał Wróblewski
 */
public class Phrase
    extends LexicalElement
{
    protected static final String TYPE = "phrase";
    protected String [] stemmedTerms;

    public Phrase(String [] stemmedTerms)
    {
        this.stemmedTerms = stemmedTerms;
    }


    protected Phrase(Element element)
    {
        String stem = element.attributeValue("stem");
        StringTokenizer stemTokenizer = new StringTokenizer(stem, " ");
        Vector stems = new Vector();

        while (stemTokenizer.hasMoreTokens())
        {
            String token = stemTokenizer.nextToken();
            stems.add(token);
        }

        stemmedTerms = new String[stems.size()];
        stemmedTerms = (String []) stems.toArray(stemmedTerms);
    }

    public boolean containsTerm(String stem)
    {
        for (int i = 0; i < stemmedTerms.length; i++)
        {
            if (stemmedTerms[i].equals(stem))
            {
                return true;
            }
        }

        return false;
    }


    // returns true if both phrases are equal !!!
    public boolean isSubphraseOrEquals(String otherPhrase)
    {
        StringTokenizer otherPhraseTokenizer = new StringTokenizer(otherPhrase, " ");

        Vector tokens = new Vector();

        while (otherPhraseTokenizer.hasMoreTokens())
        {
            String token = otherPhraseTokenizer.nextToken();
            tokens.add(token);
        }

        String [] tokensArray = new String[tokens.size()];
        tokensArray = (String []) tokens.toArray(tokensArray);

        Phrase tmpPhrase = new Phrase(tokensArray);

        for (int i = 0; i < stemmedTerms.length; i++)
        {
            if (!tmpPhrase.containsTerm(stemmedTerms[i]))
            {
                return false;
            }
        }

        return true;
    }


    // returns false if both phrases are equal !!!
    public boolean isSubphraseOf(Phrase otherPhrase)
    {
        for (int i = 0; i < stemmedTerms.length; i++)
        {
            if (!otherPhrase.containsTerm(stemmedTerms[i]))
            {
                return false;
            }
        }

        // we must check if phrases aren't equal !!!
        if (stemmedTerms.length == otherPhrase.stemmedTerms.length)
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    // checks if more than overlapThreshold words of this phrase
    // occur in another phrase
    public boolean overlapsWith(Phrase phrase, float overlapThreshold)
    {
        float overlapDegree = 0.0f;

        for (int i = 0; i < stemmedTerms.length; i++)
        {
            if (phrase.containsTerm(stemmedTerms[i]))
            {
                overlapDegree++;
            }
        }

        overlapDegree /= stemmedTerms.length;

        if (overlapDegree > overlapThreshold)
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    public int hashCode()
    {
        return (toString()).hashCode();
    }


    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append(stemmedTerms[0]);

        for (int i = 1; i < stemmedTerms.length; i++)
        {
            result.append(" ");
            result.append(stemmedTerms[i]);
        }

        return result.toString();
    }


    public Element toXML()
    {
        final DocumentFactory factory = new DocumentFactory();
        Element phrase = factory.createElement("term");
        phrase.addAttribute("type", TYPE);
        phrase.addAttribute("stem", toString());

        return phrase;
    }
}
