
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.demo.cache;

import org.carrot2.core.LocalInputComponent;

/**
 * A class representing a compound key in the cached queries map.
 * 
 * @author Dawid Weiss
 */
final class CacheEntry {
    private final LocalInputComponent input;
    private final Object equivalenceClass;
    private final String query;
    private final int requestedResults;

    public CacheEntry(LocalInputComponent input, Object equivalenceClass, String query, int requestedResults) {
        this.input = input;
        this.equivalenceClass = equivalenceClass;
        this.query = query;
        this.requestedResults = requestedResults;
    }

    /**
     * Two {@link CacheEntry} objects are equal if their queries
     * are identical and if their input components have identical class
     * (might be two different objects though).
     */
    public boolean equals(Object other) {
        if (other instanceof CacheEntry) {
            final CacheEntry otherEntry = (CacheEntry) other;
            if (this.query == null && otherEntry.query == null) {
                return true;
            } else {
                if ((query != null && equivalenceClass != null)
                        && (query.equals(otherEntry.query) && requestedResults == otherEntry.requestedResults)
                        && equivalenceClass.equals(otherEntry.equivalenceClass)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * A hash code is a query's hash code XORed with equivalence class' hash code.
     */
    public int hashCode() {
        final int hashCode = 
            (query != null ? query.hashCode() : 0) ^ equivalenceClass.hashCode() ^ requestedResults;
        return hashCode;
    }

    /**
     * Return a stringified form.
     */
    public String toString() {
        return "[CacheEntry " + input.getClass().getName()
            + "/(eqv:" + equivalenceClass + ")" + "/ " + (query != null ? query : "<null>") + "/" + requestedResults + "]";
    }
}