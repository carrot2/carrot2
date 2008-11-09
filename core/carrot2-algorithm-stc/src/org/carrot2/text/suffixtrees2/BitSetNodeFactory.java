
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.suffixtrees2;

/**
 * {@link INodeFactory} returning {@link CounterNode}s.
 */
public final class BitSetNodeFactory implements INodeFactory<BitSetNode>
{
    public BitSetNode createNode(SuffixTree<? super BitSetNode> container)
    {
        return new BitSetNode(container);
    }
}
