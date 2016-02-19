
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.carrot2.text.util.MutableCharArray;

import com.carrotsearch.hppc.ObjectHashSet;

/**
 * {@link ILexicalData} implemented on top of a hash set (stopwords) and a regular
 * expression pattern (stoplabels).
 */
final class DefaultLexicalData implements ILexicalData
{
    private final ObjectHashSet<MutableCharArray> stopwords;
    private final Pattern stoplabelPattern;

    /*
     * 
     */
    public DefaultLexicalData(ObjectHashSet<MutableCharArray> stopwords, 
                              ArrayList<Pattern> stoplabels)
    {
        this.stopwords = stopwords;
        this.stoplabelPattern = union(stoplabels);
    }

    /*
     * 
     */
    @Override
    public boolean isCommonWord(MutableCharArray word)
    {
        return stopwords.contains(word);
    }

    /*
     * 
     */
    @Override
    public boolean isStopLabel(CharSequence label)
    {
        if (this.stoplabelPattern == null)
            return false;

        return stoplabelPattern.matcher(label).matches();
    }

    /**
     * Combines a number of patterns into a single pattern with a union
     * of all of them. With automata-based pattern engines, this should
     * be faster and memory-friendly.
     */
    private static Pattern union(ArrayList<Pattern> patterns)
    {
        final StringBuilder union = new StringBuilder();
        if (patterns.size() > 0)
        {
            union.append("(");
            for (int i = 0; i < patterns.size(); i++)
            {
                if (i > 0) union.append(")|(");
                union.append(patterns.get(i).toString());
            }
            union.append(")");
            return Pattern.compile(union.toString());
        }
        else
        {
            return null;
        }
    }
}
