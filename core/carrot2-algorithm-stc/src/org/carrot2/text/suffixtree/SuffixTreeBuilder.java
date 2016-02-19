
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

package org.carrot2.text.suffixtree;


/**
 * Builds a suffix tree using method chains, thus avoiding direct dependency on
 * specialized constructors of {@link SuffixTree}.
 * 
 * @see #from(ISequence)
 * @see #build()
 */
public final class SuffixTreeBuilder
{
    /**
     * The input sequence for the tree.
     */
    private final ISequence sequence;

    /* */
    private SuffixTree.IStateCallback newStateCallback;

    /* */
    private SuffixTree.IProgressCallback progressCallback;

    /**
     * @see #from(ISequence) 
     */
    private SuffixTreeBuilder(ISequence sequence)
    {
        this.sequence = sequence;
    }

    /**
     * Returns the builder for a suffix tree made from <code>sequence</code>.
     */
    public static SuffixTreeBuilder from(ISequence sequence)
    {
        return new SuffixTreeBuilder(sequence);
    }

    /**
     * @return Return a new suffix tree according to current parameters. This method call
     *         may take a long time, depending on the length of the input sequence.
     */
    public SuffixTree build()
    {
        return new SuffixTree(sequence, newStateCallback, progressCallback);
    }

    public SuffixTreeBuilder withProgressCallback(SuffixTree.IProgressCallback callback)
    {
        this.progressCallback = callback;
        return this;
    }

    public SuffixTreeBuilder withStateCallback(SuffixTree.IStateCallback callback)
    {
        this.newStateCallback = callback;
        return this;
    }
}
