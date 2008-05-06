package org.carrot2.text.suffixtrees2;

public interface NodeFactory<T>
{
    public <E extends Node<T>> E createNode(SuffixTree<T> container);
}
