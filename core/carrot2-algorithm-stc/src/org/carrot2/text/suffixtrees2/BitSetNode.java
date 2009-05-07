
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.suffixtrees2;

import java.util.BitSet;

/**
 * A {@link Node} with an associated {@link BitSet}.
 */
public class BitSetNode extends CounterNode
{
    public final BitSet bitset = new BitSet();

    protected BitSetNode(SuffixTree<?> container)
    {
        super(container);
    }
}
