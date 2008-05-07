package org.carrot2.text.suffixtrees2;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Depth-first node iterator with postorder traversal (parent nodes after children nodes).
 */
public final class DepthFirstNodeIterator implements Iterator<Node>
{
    private final ArrayList<Node> queue = new ArrayList<Node>();

    /*
     * 
     */
    public DepthFirstNodeIterator(Node rootNode)
    {
        if (rootNode != null)
        {
            queue.add(rootNode);
        }
    }

    /*
     * 
     */
    public boolean hasNext()
    {
        return !queue.isEmpty();
    }

    /*
     * 
     */
    public Node next()
    {
        final int maxIndex = queue.size() - 1;
        Node next = queue.remove(maxIndex);
        if (next == null)
        {
            // Previous node had children and was already expanded. Consume the marker
            // and return the node.
            return queue.remove(maxIndex - 1);
        }

        if (next.isLeaf())
        {
            return next;
        }
        else
        {
            // Re-add the branching node with a visited marker.
            queue.add(next);
            queue.add(null);

            for (final Edge e : next)
            {
                queue.add(e.endNode);
            }
            
            return next();
        }
    }

    /*
     * 
     */
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
