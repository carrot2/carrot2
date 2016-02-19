
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

package org.carrot2.text.suffixtree;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.LongIntScatterMap;
import com.carrotsearch.hppc.cursors.LongIntCursor;

/**
 * Builds a suffix tree (or generalized suffix tree) on a sequence of any integers (or
 * objects that can be represented as unique integers). A direct implementation of Esko
 * Ukkonen's algorithm, but optimized for Java to use primitive data types instead of
 * objects (or boxed types).
 * 
 * @see "E. Ukkonen, On-line construction of suffix trees, Algorithmica, 1995, volume 14, number 3, pages 249-260." 
 */
public final class SuffixTree
{
    /** A constant to represent invalid suffix link from a state. */
    private final static int NO_SUFFIX_LINK = Integer.MIN_VALUE;

    /**
     * Leaf state marker in {@link #states}.
     */
    private final static int LEAF_STATE = -1;

    /**
     * Marker for the state's last edge in {@link #transitions}.
     */
    public final static int NO_EDGE = -1;

    /**
     * Root state's identifier (constant).
     */
    private final static int ROOT_STATE = 1;

    /**
     * The input sequence of integers.
     */
    final ISequence sequence;

    /**
     * Cached size of {@link #sequence}.
     */
    private final int inputSize;

    /**
     * States array indexed by state number. Values in this array are:
     * <ul>
     * <li>at build time, the suffix pointer (state pointer),</li>
     * <li>after the tree is built, the first edge from a given state (edge pointer).</li>
     * </ul>
     */
    private IntArrayList states = new IntArrayList();

    /**
     * A hash map of transitions (edges) between states in the suffix tree. The map is
     * keyed by a combination of state (upper 32 bits) and symbol (lower 32 bits). The
     * value is an index in the transitions array.
     */
    private final LongIntScatterMap transitions_map = new LongIntScatterMap(); 

    /**
     * An array of all transitions.
     * 
     * @see #addTransition(int, int, int)
     * @see #reuseTransition(int, int, int, int, int)
     */
    private final IntArrayList transitions = new IntArrayList();

    /**
     * Variables used during tree construction. See Ukkonen's algorithm for details.
     */
    private int s, k, i;
    private boolean end_point;

    /**
     * Head state and root state.
     */
    private final int head, root;

    /**
     * Default transition from head to the root.
     */
    private final int root_transition;

    /**
     * Number of integers per single transition.
     */
    private final int slots_per_transition;

    /**
     * State callback or <code>null</code>.
     * 
     * @see IStateCallback
     */
    private final IStateCallback newStateCallback;

    /**
     * A callback invoked when new states are added to the tree.
     */
    public interface IStateCallback
    {
        void newState(int state, int position);
    }

    /**
     * Progress callback is invoked when iterating forward through the input sequence
     * elements.
     */
    public interface IProgressCallback
    {
        void next(int pos);
    }

    /**
     * Visitor interface for traversals.
     * 
     * @see VisitorAdapter
     */
    public interface IVisitor
    {
        /**
         * Invoked before <code>state</code> is descended into.
         * 
         * @return Returning <code>false</code> omits the subtree of <code>state</code>.
         *         {@link #post(int)} is not invoked for this state if skipped.
         */
        public boolean pre(int state);

        /**
         * Invoked after <code>state</code> is fully traversed.
         * 
         * @param state Identifier of the completed state.
         */
        public void post(int state);

        /**
         * Invoked when an edge is visited.
         * 
         * @return Returning <code>false</code> skips the traversal of
         *         <code>toState</code>.
         */
        public boolean edge(int fromState, int toState, int startIndex, int endIndex);
    }

    /**
     * Empty implementation recursively walking the entire suffix tree.
     */
    public static class VisitorAdapter implements IVisitor
    {
        public boolean pre(int state)
        {
            return true;
        }

        public void post(int state)
        {
        }

        public boolean edge(int fromState, int toState, int startIndex, int endIndex)
        {
            return true;
        }
    }

    /**
     * Build a suffix tree for a given input sequence of symbols.
     */
    public SuffixTree(ISequence sequence, IStateCallback newStateCallback,
        final IProgressCallback progressCallback)
    {
        this.sequence = sequence;
        this.newStateCallback = newStateCallback;

        // Prepare initial conditions.
        head = createState();
        root = createState();
        setSuffixLink(root, head);
        assert ROOT_STATE == root;

        addTransition(root, 0, 0);
        slots_per_transition = transitions.size();
        root_transition = 0;

        // Build the tree.
        s = root;
        inputSize = sequence.size();
        for (k = i = 1; i <= inputSize; i++)
        {
            if (progressCallback != null) progressCallback.next(i - 1);
            update();
            canonize(s, k, i);
        }

        // Connect edges from a single state to speed up iterators.
        for (int i = states.size() - 1; i >= 0; i--)
            states.set(i, LEAF_STATE);

        for (LongIntCursor c : transitions_map)
        {
            final int g = c.value;
            final int state = (int) (c.key >>> 32);
            final int prev = states.get(state);
            if (prev != LEAF_STATE)
            {
                transitions.set(g + 3, prev);
            }
            states.set(state, g);
        }
    }

    /**
     * Update subroutine of the suffix tree building algorithm.
     */
    private final void update()
    {
        int oldr = root;
        while (true)
        {
            int r = testAndSplit(i - 1, i);
            if (end_point) break;

            createTransition(r, i, inputSize, createNewState(i));
            if (oldr != root) setSuffixLink(oldr, r);
            oldr = r;

            canonize(getSuffixLink(s), k, i - 1);
        }

        if (oldr != root) setSuffixLink(oldr, s);
    }

    /**
     * Test and split subroutine of the suffix tree building algorithm.
     */
    private final int testAndSplit(int p, int ti)
    {
        if (k <= p)
        {
            final int g = findTransition(s, k);
            assert g >= 0;

            final int gk = transitions.get(g + 1);
            final int gj = transitions.get(g + 2);
            final int gs = transitions.get(g);

            if (sequence.objectAt(ti - 1) == sequence.objectAt(gk + p - k))
            {
                end_point = true;
                return s;
            }
            else
            {
                final int r = createNewState(gk + p - k);
                reuseTransition(removeTransition(s, k), s, gk, gk + p - k, r);
                createTransition(r, gk + p - k + 1, gj, gs);
                end_point = false;
                return r;
            }
        }
        else
        {
            end_point = findTransition(s, ti) >= 0;
            return s;
        }
    }

    /**
     * Canonization subroutine of the suffix tree building algorithm.
     */
    private void canonize(int s, int k, int p)
    {
        if (p >= k)
        {
            int g = findTransition(s, k);
            int d;
            while (g >= 0 && (d = transitions.get(g + 2) - transitions.get(g + 1)) <= p - k)
            {
                k = k + d + 1;
                s = transitions.get(g);
                if (k <= p) g = findTransition(s, k);
            }
        }

        this.s = s;
        this.k = k;
    }

    /*
     * 
     */
    private void setSuffixLink(int fromState, int toState)
    {
        states.set(fromState, toState);
    }

    /*
     * 
     */
    private int getSuffixLink(int s)
    {
        final int ts = this.states.get(s);
        assert ts != NO_SUFFIX_LINK;
        return ts;
    }

    /**
     * Add a new state to the tree, calling external callback if requested.
     */
    private final int createNewState(int position)
    {
        final int state = createState();
        if (newStateCallback != null)
        {
            newStateCallback.newState(state, position);
        }
        return state;
    }

    /**
     * Adds a new state to the list of {@link #states}.
     */
    private final int createState()
    {
        final int state = states.size();
        states.add(NO_SUFFIX_LINK);
        return state;
    }

    /**
     * Create a transition from state <code>s</code> to state <code>ts</code>, labeled
     * with symbols between <code>k</code> and <code>p</code> (1-based, inclusive).
     */
    private final void createTransition(int s, int k, int p, int ts)
    {
        assert k > 0 && p > 0;

        final int transition = addTransition(ts, k, p);
        transitions_map.put(asLong(s, sequence.objectAt(k - 1)), transition);
    }

    /**
     * Reuse an existing transition slot to store a transition from state <code>s</code>
     * to state <code>ts</code>, labeled with symbols between <code>k</code> and
     * <code>p</code> (1-based, inclusive).
     */
    private final void reuseTransition(int transition, int s, int k, int p, int ts)
    {
        assert k > 0 && p > 0;

        transitions.set(transition, ts);
        transitions.set(transition + 1, k);
        transitions.set(transition + 2, p);
        transitions_map.put(asLong(s, sequence.objectAt(k - 1)), transition);
    }

    /**
     * Adds a transition to state <code>ts</code>, labeled with symbols between
     * <code>k</code> and <code>p</code> (1-based, inclusive), but does not add hash map
     * entry (for internal use).
     */
    private final int addTransition(int ts, int k, int p)
    {
        final int transition = transitions.size();
        transitions.add(ts);
        transitions.add(k);
        transitions.add(p);
        transitions.add(NO_EDGE);
        return transition;
    }

    /**
     * Find a transition from state <code>s</code>, labeled with symbol at index
     * <code>k - 1</code> in the input sequence.
     */
    private final int findTransition(int s, int k)
    {
        return s == head ? root_transition : findEdge(s, sequence.objectAt(k - 1));
    }

    /**
     * Remove the transition from state <code>s</code>, labeled with symbol at index
     * <code>k - 1</code> and return its slot in the transitions array.
     */
    private int removeTransition(int s, int k)
    {
        assert s != head;
        return transitions_map.remove(asLong(s, sequence.objectAt(k - 1)));
    }

    /**
     * Make a <code>long</code> from two integers.
     */
    private final static long asLong(int i1, int i2)
    {
        return ((long) i1) << 32 | (i2 & 0xffffffffL);
    }

    /**
     * @return Return the number of transitions (edges) in the tree.
     */
    public final int getTransitionsCount()
    {
        return (this.transitions.size() / slots_per_transition) - 1;
    }

    /**
     * @return Return the number of states in the tree.
     */
    public final int getStatesCount()
    {
        return this.states.size() - 1;
    }

    /**
     * @return <code>true</code> if this suffix tree has a path from the root state to a
     *         leaf state corresponding to a given sequence of objects. This indicates the
     *         input sequence had a suffix identical to <code>sequence</code>.
     */
    public boolean containsSuffix(ISequence seq)
    {
        int state = root;
        int i = 0;
        while (true)
        {
            // Find an edge leaving the current state marked with symbol sequence[i].
            final int edge = findEdge(state, seq.objectAt(i));
            if (edge < 0)
            {
                // Different characters on explicit state.
                return false;
            }

            // Follow the edge, checking symbols on the way.
            int j = getStartIndex(edge);
            final int m = getEndIndex(edge) + 1;
            for (;i < seq.size() && j < m; j++, i++)
            {
                if (seq.objectAt(i) != this.sequence.objectAt(j))
                {
                    // Different characters on implicit state.
                    return false;
                }
            }

            if (i == seq.size())
            {
                // End of input sequence must be aligned with the tree's leaf state.
                return j == inputSize;
            }

            // Follow to the child state.
            state = getToState(edge);
        }
    }

    /**
     * Walks the states and edges of the suffix tree, depth-first.
     */
    public final void visit(final IVisitor visitor)
    {
        visitState(root, visitor);
    }

    /**
     * Start visiting from a given state.
     */
    public final void visitState(final int state, final IVisitor visitor)
    {
        if (visitor.pre(state))
        {
            int edge = firstEdge(state);
            while (edge != NO_EDGE)
            {
                final int toState = transitions.get(edge);
                if (visitor.edge(state, toState, getStartIndex(edge), getEndIndex(edge)))
                {
                    visitState(toState, visitor);
                }
                edge = nextEdge(edge);
            }
            visitor.post(state);
        }
    }

    /**
     * For procedural traversals (not visitors).
     */
    public int getRootState()
    {
        return root;
    }

    /**
     * Check if <code>state</code> is a leaf (has no outgoing edges).
     */
    public final boolean isLeaf(int state)
    {
        return this.states.get(state) == LEAF_STATE;
    }

    /**
     * Returns the index of the first edge from a given state or {@link #NO_EDGE} if a
     * given state has no edges. Does not perform any sanity check on the input state.
     */
    public final int firstEdge(int state)
    {
        return states.get(state);
    }

    /**
     * Returns the index of the next edge (sibling) or {@link #NO_EDGE} if
     * <code>edge</code> is the last edge in its state.
     */
    public final int nextEdge(int edge)
    {
        return transitions.get(edge + 3);
    }

    /**
     * Find a transition from state <code>state</code>, labeled with a given symbol.
     * {@link #NO_EDGE} is returned if there is no such edge.
     */
    public final int findEdge(int state, int symbol)
    {
        return transitions_map.getOrDefault(asLong(state, symbol), NO_EDGE);
    }

    /**
     * Returns the target state for a given edge.
     */
    public int getToState(int edge)
    {
        return transitions.get(edge);
    }

    /**
     * Returns the edge label's start index (inclusive).
     */
    public int getStartIndex(int edge)
    {
        return transitions.get(edge + 1) - 1;
    }

    /**
     * Returns the edge label's end index (inclusive).
     */
    public int getEndIndex(int edge)
    {
        return transitions.get(edge + 2) - 1;
    }
}
