
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.dawidweiss.carrot.filter.stc.suffixtree;

/**
 * The interface providing access to collection elements for use by SuffixTree.
 * 
 * In order to be used by SuffixTree class (and subclasses), the object must
 * implement this interface.
 * 
 * @author Dawid Weiss
 */
public interface SuffixableElement {
    public static final Object END_OF_SUFFIX = new Object() {
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            return false;
        }

        public String toString() {
            return ("EOS");
        }
    };

    /**
     * Returns an object at specified index in this collection. Object must
     * implement {@link Comparable} interface.
     */
    public Object get(int index);

    /**
     * Returns this collection's length.
     */
    public int size();
}

