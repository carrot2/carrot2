package org.carrot2.text.suffixtrees2;

/**
 * {@link NodeFactory} returning {@link CounterNode}s.
 */
public final class CounterNodeFactory implements NodeFactory
{
    public Node createNode(SuffixTree owner)
    {
        return new CounterNode(owner);
    }
}
