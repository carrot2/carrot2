package org.carrot2.text.suffixtrees2;

/**
 * Implementation of a generalized suffix tree (a suffix tree that holds more than one
 * sequence and remembers documents from which each suffix in the tree comes from).
 * <p>
 * The implementation uses the simplest of many possible techniques: it concatenates input
 * sequences and inserts artificial delimiters between them.
 */
public final class GeneralizedSuffixTree<T extends BitSetNode> extends SuffixTree<T>
{
    /**
     * Concatenated sequence of object symbols and separators.
     */
    public int [] concatenated;

    /*
     * 
     */
    public GeneralizedSuffixTree(NodeFactory<? extends T> nodeFactory)
    {
        super(nodeFactory);
    }

    /**
     * Create a generalized suffix tree for more than one sequence of elements.
     * <b>Sequences must return non-negative object codes</b> because artificial sequence
     * separators are negative and start from <code>-1</code>.
     */
    public void build(Sequence... sequences)
    {
        /*
         * Calculate total size of the concatenated sequence.
         */
        int totalSize = sequences.length;
        for (final Sequence s : sequences)
        {
            totalSize += s.size();
        }

        /*
         * Concatenate input sequences and glue them with unique artificial separator
         * codes.
         */
        this.concatenated = new int [totalSize];
        int sequenceNumber = 0;
        int j = 0;
        for (final Sequence s : sequences)
        {
            final int max = s.size();
            for (int i = 0; i < max; i++)
            {
                concatenated[j++] = s.objectAt(i);
            }

            concatenated[j++] = ~sequenceNumber;
            sequenceNumber++;
        }

        /*
         * Build a suffix tree from the concatenated sequence.
         */
        super.build(new IntSequence(concatenated));

        /*
         * Update (trim) artificial suffixes spanning more than one original sequence (at
         * leaf nodes).
         */
        for (BitSetNode n : this)
        {
            if (n.isLeaf())
            {
                int index = n.getSuffixStartIndex();
                while (concatenated[index] >= 0)
                {
                    index++;
                }

                n.edgeToParent.lastElementIndex = index;
            }
        }

        /*
         * Update document counts and leaf counts under each node.
         */
        for (BitSetNode n : this)
        {
            if (n.isLeaf())
            {
                final int separatorCode = concatenated[n.getSuffixEndIndex()];
                n.bitset.set(~separatorCode);
                n.count = 1;
            }

            final BitSetNode parent = (BitSetNode) n.getParentNode();
            if (parent != null)
            {
                parent.bitset.or(n.bitset);
                parent.count += n.count;
            }
        }
    }
}
