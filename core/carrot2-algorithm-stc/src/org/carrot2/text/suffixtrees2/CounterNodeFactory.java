package org.carrot2.text.suffixtrees2;

/**
 * {@link NodeFactory} returning {@link CounterNode}s.
 */
public final class CounterNodeFactory implements NodeFactory<CounterNode>
{
    public CounterNode createNode(SuffixTree<? super CounterNode> container)
    {
        return new CounterNode(container);
    }
}
