package org.carrot2.text.suffixtrees2;

/**
 * Internal nodes factory for {@link SuffixTree}.
 */
public interface NodeFactory<T extends Node>
{
    public T createNode(SuffixTree<? super T> container);
}
