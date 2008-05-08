package org.carrot2.text.suffixtrees2;

/**
 * Default {@link NodeFactory} returning {@link Node}s.
 */
public final class DefaultNodeFactory implements NodeFactory<Node>
{
    public Node createNode(SuffixTree<? super Node> owner)
    {
        return new Node(owner);
    }
}
