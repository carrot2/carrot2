package org.carrot2.util;

import com.carrotsearch.hppc.BoundedProportionalArraySizingStrategy;
import com.carrotsearch.hppc.IntStack;

/**
 * Chained representation of document-frequency lists. The core idea is to store chained
 * triples of entries:
 * 
 * <pre>
 * document_id, count, next_entry_ptr
 * </pre>
 * 
 * A phrase points to one entry in the list and all the remaining entries (all documents
 * in which a phrase exists) are linked from that head entry. There are two gains from
 * such a structure:
 * <ol>
 * <li>Because symbols are added to the detection tree in the documents order (or in
 * reverse documents order) then the <code>count</code> field can change for the head
 * entry <i>only</i> (there is no need for lookups at the time of detecting phrases). When
 * a new document starts to be processed and an existing phrase has been hit, we simply
 * replace the head entry with a new one and link back to the previous entry (which will
 * never change again).</li>
 * <li>We can store all entries (for all "chains" starting at any "head" entry) in the
 * same linear array because they don't conflict with each other.</li>
 * </ol>
 */
public final class LinkedFrequencyList
{
    private final static int MB = 1024 * 1024;

    public static final int NO_NEXT = -1;

    // TODO: Because of the immutability of entries down the chain we could store
    // them in a different area in a packed (vcoded) format to save some memory.
    // To be considered if really needed.

    /** All chained entries in triples: (document, count, next_entry_ptr) */
    private IntStack list = new IntStack(IntStack.DEFAULT_CAPACITY,
        new BoundedProportionalArraySizingStrategy(1 * MB / 4, 20 * MB / 4, 1.5f));

    /**
     * @param entry The current head entry or {@link #NO_NEXT} if a new head entry (chain)
     *            should be added.
     * @param document The current document.
     */
    public int addOne(int entry, int document)
    {
        if (entry == NO_NEXT)
        {
            // No previous entry, create a new one.
            entry = list.size();
            list.push(document, 1, NO_NEXT);
        }
        else
        {
            if (list.get(entry) == document)
            {
                // Same document, update the count only.
                list.buffer[entry + 1]++;
            }
            else
            {
                // Different document, create a new entry and link it to the previous one.
                final int previousEntry = entry;
                entry = list.size();
                list.push(document, 1, previousEntry);
            }
        }
        return entry;
    }

    /**
     * Collect document-tf pairs.
     */
    public void collect(IntStack docTfs, int entry)
    {
        while (entry != NO_NEXT)
        {
            docTfs.push(list.get(entry), list.get(entry + 1));
            entry = list.get(entry + 2);
        }
    }
}
