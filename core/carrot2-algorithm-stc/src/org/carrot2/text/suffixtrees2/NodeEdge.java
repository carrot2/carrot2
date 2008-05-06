package org.carrot2.text.suffixtrees2;

/**
 * 
 */
final class NodeEdge
{
    Node<?> node;
    Object key;
    NodeEdge next;

    public NodeEdge()
    {
    }

    public NodeEdge(Node<?> node, Object key)
    {
        this.node = node;
        this.key = key;
    }

    @Override
    public boolean equals(Object obj)
    {
        final NodeEdge other = (NodeEdge) obj;
        return (other.node == this.node &&
            other.key.equals(this.key));
    }

    @Override
    public int hashCode()
    {
        return node.hashCode() ^ key.hashCode();
    }
}
