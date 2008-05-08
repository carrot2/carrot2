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
