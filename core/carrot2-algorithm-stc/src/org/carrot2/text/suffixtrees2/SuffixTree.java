package org.carrot2.text.suffixtrees2;

import java.util.HashMap;
import java.util.List;

/**
 * This class implements a generic Esko Ukkonnen's algorithm for creating a suffix tree.
 * The implementation has been derived from the C-version by Mark Nelson:
 * <p>
 * <b>Fast String Searching With Suffix Trees <i>Mark Nelson </i></b> Dr Dobb's Journal,
 * August 1996 http://softlab.od.ua/algo/data/suftrees/suffixt.htm (2000)
 * <p>
 * It is obvious that the performance of this class will <b>highly</b> depend on the type
 * of sequence collection.
 */
public final class SuffixTree<T>
{
    /**
     * {@link Node} factory for internal tree nodes.
     */
    final NodeFactory<T> nodeFactory;

    /**
     * A hash map of edges leaving each node.
     */
    final HashMap<NodeEdge, Edge<T>> edges = new HashMap<NodeEdge, Edge<T>>();

    /**
     * This is the root node of the tree.
     */
    Node<T> rootNode;

    /**
     * Sequence of elements to consider.
     */
    Object [] sequence;

    /*
     * 
     */
    @SuppressWarnings("unchecked")
    public SuffixTree()
    {
        this(new DefaultNodeFactory());
    }

    /*
     * 
     */
    public SuffixTree(NodeFactory<T> nodeFactory)
    {
        this.nodeFactory = nodeFactory;
    }

    /**
     * Creates a suffix tree for the given list of elements. Returns the root node of the
     * resulting tree.
     */
    public Node<T> build(List<T> sequence)
    {
        rootNode = nodeFactory.createNode(this);
        edges.clear();

        final Suffix activePoint = new Suffix(this, rootNode, 0, -1);

        // Loop through all prefixes of the input.
        this.sequence = sequence.toArray(new Object [sequence.size()]);
        for (int i = 0; i < this.sequence.length; i++)
        {
            insertPrefix(activePoint, i);
        }

        return rootNode;
    }

    /**
     * The heart of Ukkonnen's algorithm. Inserts a single prefix to the tree while
     * retaining its structure. The source code to this function has comments from Mark
     * Nelson's excellent paper published in Dr Dobb's journal.
     * 
     * @param active Active point in the suffix tree.
     * @param lastElementIndex The index of currently inserted element from
     *            getCurrentElement().
     * @return current end point (becomes active point if more elements are to be
     *         inserted).
     */
    protected Suffix insertPrefix(Suffix active, int lastElementIndex)
    {
        Node parent_node = null;
        Node last_parent_node = null;

        while (true)
        {
            Edge edge;
            parent_node = active.originNode;

            /*
             * Step 1 is to try and find a matching edge for the given node. If a matching
             * edge exists, we are done adding edges, so we break out of this big loop.
             */
            if (active.isExplicit())
            {
                /*
                 * Explicit node, check if it has an edge starting with current element.
                 */
                edge = active.originNode.findEdgeMatchingFirstElement(
                    sequence[lastElementIndex]);

                /*
                 * If it does, do nothing (path compression, an implicit node is created).
                 */
                if (edge != null) break;
            }
            else
            {
                /*
                 * implicit node, a little more complicated because we have to split the
                 * edge and create a new node IF the implicit node doesn't match inserted
                 * element.
                 */
                edge = active.originNode
                    .findEdgeMatchingFirstElement(sequence[active.firstElementIndex]);

                int span = active.lastElementIndex - active.firstElementIndex;

                if (sequence[edge.firstElementIndex + span + 1]
                    .equals(sequence[lastElementIndex]))
                {
                    break;
                }

                /*
                 * We must split the edge. The newly created node becomes parent node.
                 */
                parent_node = edge.splitEdge(active);
            }

            /*
             * We didn't find a matching edge, so we create a new one, add it to the tree
             * at the parent node position, and insert it into the hash table. When we
             * create a new node, it also means we need to create a suffix link to the new
             * node from the last node we visited.
             */
            parent_node.createChildNode(lastElementIndex, sequence.length - 1);

            if (last_parent_node != null && last_parent_node != rootNode)
            {
                last_parent_node.suffixNode = parent_node;
            }

            last_parent_node = parent_node;

            /* This final step is where we move to the next smaller suffix */
            if (active.originNode == rootNode)
            {
                active.firstElementIndex++;
            }
            else
            {
                active.originNode = active.originNode.suffixNode;
            }

            active.canonize();
        }

        if (last_parent_node != null && last_parent_node != rootNode)
        {
            last_parent_node.suffixNode = parent_node;
        }

        /* Now the end point is the next active point. */
        active.lastElementIndex++;
        active.canonize();

        return active;
    }
    
    private NodeEdge temp = new NodeEdge();
    
    /*
     * 
     */
    final NodeEdge removeEdge(Node<T> node, Object label)
    {
        this.temp.node = node;
        this.temp.key = label;

        this.edges.remove(temp);
        return temp;
    }

    /*
     * 
     */
    final Edge<T> getEdge(Node<T> node, Object label)
    {
        this.temp.node = node;
        this.temp.key = label;
        return this.edges.get(temp);
    }    
}
