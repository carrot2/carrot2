
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering.stc;

import static org.carrot2.text.suffixtree.SuffixTree.NO_EDGE;

import java.util.ArrayList;

import org.carrot2.text.suffixtree.ISequence;
import org.carrot2.text.suffixtree.IntegerSequence;
import org.carrot2.text.suffixtree.SuffixTree;
import org.carrot2.text.suffixtree.SuffixTreeBuilder;

import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.IntStack;

/**
 * A suffix tree dedicated to finding frequent phrases in documents.
 */
final class GeneralizedSuffixTree
{
    /**
     * Builds an {@link ISequence} suitable for detection of frequently occurring phrases
     * in many documents using a {@link SuffixTree}. Marks ends of phrases with unique
     * symbols and stores the information about document boundaries.
     */
    static class SequenceBuilder
    {
        private int separator = -1;

        public final IntStack input = new IntStack();

        /**
         * Positions in {@link #input} where documents end.
         */
        public IntStack documentMarkers = new IntStack();

        /**
         * We keep the document number for each leaf state.
         */
        public IntStack stateOriginDocument = new IntStack();

        /**
         * A suffix tree built from the input phrases.
         */
        public SuffixTree stree;

        /**
         * Callbacks for marking leaf states.
         */
        private final class LeafStateMarker 
            implements SuffixTree.IStateCallback, SuffixTree.IProgressCallback
        {
            private int currentDocument = 0;
            private int markerIndex = 0;

            public void next(int pos)
            {
                if (pos == documentMarkers.get(markerIndex))
                {
                    currentDocument++;
                    markerIndex++;
                }
            }
            
            public void newState(int state, int position)
            {
                while (stateOriginDocument.size() < state) 
                    stateOriginDocument.push(-1);
                stateOriginDocument.push(currentDocument);
            }
        }
        
        /**
         * 
         */
        public void addPhrase(int [] terms, int start, int len)
        {
            input.push(terms, start, len);
            input.push(separator--);
        }

        /**
         * 
         */
        public void addPhrase(int... terms)
        {
            addPhrase(terms, 0, terms.length);
        }

        /**
         * 
         */
        public void endDocument()
        {
            documentMarkers.push(input.size());
        }

        /**
         * 
         */
        public void buildSuffixTree()
        {
            this.stateOriginDocument.clear();

            final LeafStateMarker marker = new LeafStateMarker();
            final ISequence seq = new IntegerSequence(input.buffer, 0, input.elementsCount);
            this.stree = SuffixTreeBuilder.from(seq)
                .withProgressCallback(marker)
                .withStateCallback(marker)
                .build();
        }
    }

    /**
     * Recursive walk over the suffix tree (with additional information provided by 
     * {@link SequenceBuilder}), extracting paths that occurred more than once.
     */
    static abstract class Visitor
    {
        /** Path from the root (edges index ranges) when walking through the tree. */
        private final IntStack edges = new IntStack();
        
        /** Bitsets used to compute cardinality in each node. */
        private final ArrayList<BitSet> bsets = new ArrayList<BitSet>();
        
        /** Suffix tree on all the input.*/
        private final SuffixTree stree;
        
        /** Sequence builder with the input. */
        protected final SequenceBuilder sb;

        /** Minimum cardinality (inclusive) in an internal state to visit it. */
        private int minCardinality;

        public Visitor(SequenceBuilder sb, int minCardinality)
        {
            assert minCardinality > 1;

            this.stree = sb.stree;
            this.sb = sb;
            this.minCardinality = minCardinality;
        }

        public void visit()
        {
            // In a suffix tree without any documents, this will be the case. 
            if (stree.isLeaf(stree.getRootState()))
                return;

            countDocs(0, stree.getRootState());
        }

        private void countDocs(int level, int state)
        {
            assert !stree.isLeaf(state);

            final BitSet me = getBitSet(level);
            for (int edge = stree.firstEdge(state); edge != NO_EDGE; edge = stree.nextEdge(edge))
            {
                final int childState = stree.getToState(edge);
                if (stree.isLeaf(childState))
                {
                    final int documentIndex = sb.stateOriginDocument.get(childState);
                    me.set(documentIndex);
                }
                else
                {
                    final BitSet child = getBitSet(level + 1);
                    child.clear();
                    edges.push(stree.getStartIndex(edge), stree.getEndIndex(edge));
                    countDocs(level + 1, childState);
                    edges.discard(2);
                    me.or(child);
                }
            }

            if (stree.getRootState() != state)
            {
                final int card = (int) me.cardinality();
                if (card >= minCardinality)
                {
                    visit(state, card, me, edges);
                }
            }
        }

        protected abstract void visit(int state, int cardinality, BitSet documents, IntStack path);

        private BitSet getBitSet(int level)
        {
            while (bsets.size() <= level) bsets.add(new BitSet());
            return bsets.get(level);
        }
    };

    /* */
    private GeneralizedSuffixTree()
    {
    }
}
