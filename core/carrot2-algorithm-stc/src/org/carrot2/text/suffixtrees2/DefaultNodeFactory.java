package org.carrot2.text.suffixtrees2;

/**
 * Default {@link NodeFactory} returning {@link Node}s.
 */
@SuppressWarnings("unchecked")
public final class DefaultNodeFactory implements NodeFactory
{
    public Node createNode(SuffixTree owner)
    {
        return new Node(owner);
    }
}
