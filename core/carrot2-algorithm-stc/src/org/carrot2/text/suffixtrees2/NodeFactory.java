package org.carrot2.text.suffixtrees2;

/**
 * Internal nodes factory for {@link SuffixTree}.
 */
public interface NodeFactory
{
    public Node createNode(SuffixTree container);
}
