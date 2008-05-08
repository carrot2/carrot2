package org.carrot2.text.suffixtrees2;

/**
 * {@link NodeFactory} returning {@link CounterNode}s.
 */
public final class BitSetNodeFactory implements NodeFactory<BitSetNode>
{
    @Override
    public BitSetNode createNode(SuffixTree<? super BitSetNode> container)
    {
        return new BitSetNode(container);
    }
}
