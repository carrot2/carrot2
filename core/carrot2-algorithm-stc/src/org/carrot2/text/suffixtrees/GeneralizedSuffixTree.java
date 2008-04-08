package org.carrot2.text.suffixtrees;

import java.util.ArrayList;

/**
 * <p>
 * This class implements a <i>generalized suffix tree</i> data structure. In a GST, all
 * suffixes of one or more {@link SuffixableElement}s are present in the tree..
 */
public class GeneralizedSuffixTree extends SuffixTree
{
    /**
     * An {@link ArrayList} of {@link SuffixableElement} elements inserted to this tree.
     */
    private ArrayList<SuffixableElement> allElements = new ArrayList<SuffixableElement>();

    /**
     * Creates a new {@link GSTNode}.
     */
    protected Node createNode()
    {
        return new GSTNode(this);
    }

    /**
     * Adds a single {@link SuffixableElement} to the tree.
     */
    public Node add(SuffixableElement element)
    {
        Suffix activePoint;

        /* Add a new SuffixableElement to the array of stored elements */
        allElements.add(element);

        if (rootNode == null)
        {
            rootNode = createNode();

            rootNode.setEdgeToParent(null);

            // start inserting from first prefix and root node.
            activePoint = new Suffix(this, rootNode, 0, -1);

            // loop through all prefixes.
            for (int i = 0; i < getCurrentElement().size(); i++)
                insertPrefix(activePoint, i);
        }
        else
        {
            /*
             * Find out at which node the longest matching prefix of current
             * SuffixableElement ends - this will be the active node we'll resume
             * inserting.
             */
            Node lastPrefixNode = rootNode;
            Edge follow = null;
            int startIndex;
            int endIndex = 0;

            for (startIndex = 0; startIndex < getCurrentElement().size();)
            {
                // is there any Edge we can follow?
                follow = lastPrefixNode.findEdgeMatchingEntirely(getCurrentElement(),
                    startIndex);

                if (follow == null)
                {
                    endIndex = startIndex - 1;

                    // check if it maybe was an implicit node somewhere along
                    // the edge
                    follow = lastPrefixNode.findEdgeMatchingFirstElement(startIndex);

                    if (follow == null) break;

                    // advance index to that implicit node and break the loop
                    for (int i = follow.getStartIndex(); i <= follow.getEndIndex(); i++)
                    {
                        if (follow.getEndNode().getSuffixableElement().get(i).equals(
                            this.getCurrentElement().get(endIndex + 1)) == false)
                        {

                            // first non-matching character (implicit node on
                            // compressed path).
                            break;
                        }

                        endIndex++;
                    }

                    break;
                }
                else
                {
                    // advance pointer
                    lastPrefixNode = follow.getEndNode();
                    startIndex += follow.length();
                }
            }

            if (startIndex == getCurrentElement().size())
            {
                // POSSIBILITY OF PERFORMANCE TUNING:
                // iterate through elements of the boundary path only.
                for (int i = 0; i < getCurrentElement().size(); i++)
                {
                    activePoint = new Suffix(this, rootNode, i, getCurrentElement()
                        .size() - 1);

                    activePoint.canonize();
                    ((GSTNode) activePoint.originNode)
                        .addIndexedElement(getCurrentElementNumber());
                }

                return rootNode;
            }
            else
            {
                // insert remaining suffixes.
                activePoint = new Suffix(this, lastPrefixNode, startIndex, endIndex);

                // loop through all prefixes.
                for (int i = endIndex + 1; i < getCurrentElement().size(); i++)
                    insertPrefix(activePoint, i);
            }
        }

        return rootNode;
    }

    /**
     * Returns the SuffixableElement which was added to the tree as the index-th one.
     */
    protected SuffixableElement getElementByIndex(int index)
    {
        return (SuffixableElement) allElements.get(index);
    }

    /**
     * Returns the currently processed SuffixableElement.
     */
    protected SuffixableElement getCurrentElement()
    {
        return (SuffixableElement) this.allElements.get(getCurrentElementNumber());
    }

    /**
     * Returns the number of currently inserted {@link SuffixableElement}.
     */
    protected int getCurrentElementNumber()
    {
        return allElements.size() - 1;
    }
}
