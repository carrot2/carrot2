

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


package com.stachoodev.carrot.filter.cluster.common;


import java.util.*;


/**
 *
 */
public class DefaultPreprocessingStrategy
    implements PreprocessingStrategy
{
    /** The sentence delimiters over which phrases cannot be spanned */
    private static final String [] DEFAULT_SENTENCE_DELIMITERS = { ".", "?", "!", ";", "|" };

    /** Sentence delimiters */
    private String [] sentenceDelimiters;

    /** */
    protected HashMap stems;
    protected HashSet strongWords;

    /**
     * @see java.lang.Object#Object()
     */
    public DefaultPreprocessingStrategy()
    {
        this(DEFAULT_SENTENCE_DELIMITERS);
    }


    /**
     * Method DefaultPreprocessingStrategy.
     *
     * @param sentenceDelimiters
     */
    public DefaultPreprocessingStrategy(String [] sentenceDelimiters)
    {
        this.sentenceDelimiters = sentenceDelimiters;
    }

    /**
     * @see com.stachoodev.carrot.filter.cluster.common.PreprocessingStrategy#preprocess(com.stachoodev.carrot.filter.cluster.common.Snippet)
     */
    public Snippet [] preprocess(AbstractClusteringContext clusteringContext)
    {
        Snippet [] snippets = clusteringContext.getSnippets();
        Snippet [] preprocessedSnippets = new Snippet[snippets.length];

        stems = ((DefaultClusteringContext) clusteringContext).getStems();
        strongWords = clusteringContext.getStrongWords();

        for (int i = 0; i < snippets.length; i++)
        {
            preprocessedSnippets[i] = preprocess(snippets[i]);
        }

        return preprocessedSnippets;
    }


    /**
     * Method clean.
     *
     * @param string
     *
     * @return String
     */
    protected Snippet preprocess(Snippet snippet)
    {
        return new Snippet(
            snippet.getId(), preprocess(snippet.getTitle(), true),
            preprocess(snippet.getBody(), false)
        );
    }


    /**
     * @param stringBuffer
     * @param stems
     * @param text
     */
    private String preprocess(String text, boolean isTitle)
    {
        StringBuffer stringBuffer = new StringBuffer();
        StringTokenizer stringTokenizer = new StringTokenizer(text);

        boolean appendDelimiter = false;
        boolean prependDelimiter = false;
        boolean delimiterAdded = false;

        while (stringTokenizer.hasMoreTokens())
        {
            String token = stringTokenizer.nextToken();

            // Make the token lowercase
            token = token.toLowerCase();

            // Remove delimiters from the beginning
            boolean moreDelimiters = true;

            while (moreDelimiters)
            {
                moreDelimiters = false;

                for (int i = 0; i < sentenceDelimiters.length; i++)
                {
                    if (token.startsWith(sentenceDelimiters[i]))
                    {
                        prependDelimiter = true;
                        moreDelimiters = true;
                        token = token.substring(1);

                        break;
                    }
                }
            }

            // Remove delimiters from the end
            moreDelimiters = true;

            while (moreDelimiters)
            {
                moreDelimiters = false;

                for (int i = 0; i < sentenceDelimiters.length; i++)
                {
                    if (token.endsWith(sentenceDelimiters[i]))
                    {
                        token = token.substring(0, token.length() - 1);
                        moreDelimiters = true;
                        appendDelimiter = true;

                        break;
                    }
                }
            }

            if (stems.containsKey(token))
            {
                token = (String) stems.get(token);
            }

            if (isTitle)
            {
                strongWords.add(token);
            }

            if (prependDelimiter && (stringBuffer.length() != 0) && !delimiterAdded) // don't prepend the "." at the beginning of the string
            {
                stringBuffer.append(". ");
                delimiterAdded = true;
            }

            if (token.length() > 0)
            {
                stringBuffer.append(token);
                stringBuffer.append(" ");
                delimiterAdded = false;
            }

            if (appendDelimiter && stringTokenizer.hasMoreTokens() && !delimiterAdded) // don't append the "." at the end of the string
            {
                stringBuffer.append(". ");
                delimiterAdded = true;
            }

            prependDelimiter = false;
            appendDelimiter = false;
        }

        return stringBuffer.toString().trim();
    }
}
