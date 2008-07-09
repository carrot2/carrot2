package org.carrot2.text.suffixtrees2;

/**
 * A key in the hash map of all edges in the tree.
 * 
 * @see SuffixTree#edges
 */
final class NodeEdge
{
    Node node;
    int objectCode;
    NodeEdge next;

    public NodeEdge()
    {
    }

    public NodeEdge(Node node, int objectCode)
    {
        this.node = node;
        this.objectCode = objectCode;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        
        if (obj instanceof NodeEdge)
        {
            final NodeEdge other = (NodeEdge) obj;
            return (other.node == this.node && (this.objectCode == other.objectCode));
        }
        
        return false;
    }

    @Override
    public int hashCode()
    {
        return node.hashCode() ^ objectCode;
    }
}
