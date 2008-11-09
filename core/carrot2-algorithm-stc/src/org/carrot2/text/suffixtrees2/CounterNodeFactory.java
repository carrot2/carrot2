
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
public final class CounterNodeFactory implements INodeFactory<CounterNode>
{
    public CounterNode createNode(SuffixTree<? super CounterNode> container)
    {
        return new CounterNode(container);
    }
}
