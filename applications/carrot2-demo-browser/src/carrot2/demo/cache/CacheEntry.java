package carrot2.demo.cache;

import com.dawidweiss.carrot.core.local.LocalInputComponent;

/**
 * A class representing a compound key in the cached queries map.
 * 
 * @author Dawid Weiss
 */
final class CacheEntry {
    private final LocalInputComponent input;
    private final String query;

    public CacheEntry(LocalInputComponent input, String query) {
        this.input = input;
        this.query = query;
    }

    /**
     * Two {@link CacheEntry} objects are equal if their queries
     * are identical and if their input components have identical class
     * (might be two different objects though).
     */
    public boolean equals(Object other) {
        if (other instanceof CacheEntry) {
            final CacheEntry otherEntry = (CacheEntry) other;
            return ((this.query == null && otherEntry.query == null)
                    || (query.equals(otherEntry.query)
                        && input.getClass().equals(otherEntry.input.getClass()))); 
        } else return false;
    }

    /**
     * A hash code is a query's hash code XORed with input class' has code.
     */
    public int hashCode() {
        final int hashCode = 
            (query != null ? query.hashCode() : 0) ^ input.getClass().hashCode();
        return hashCode;
    }

    /**
     * Return a stringified form.
     */
    public String toString() {
        return "[CacheEntry " + input.getClass().getName()
            + "@" + input.getClass().hashCode() + "/ " + (query != null ? query : "<null>") + "]";
    }
}