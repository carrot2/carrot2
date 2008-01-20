
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.trc.carrot.filter.cluster.rough.filter.ngram;

import org.carrot2.filter.trc.util.ArrayUtils;


/**
 * Representation of a n-gram
 */
public class NGram {

    /**
     * reference to original txt source
     */
    String[] words;
    protected int lazyHashCode = 17;
    protected boolean hashCalculated = false;
    /**
     * index of position where n-gram starts and its length
     */

    public NGram (String[] source, int startIndex, int length) {
        words = new String[length];
        for (int i=0; i < length; i++) {
            words[i] = source[startIndex+i];
        }
    }
    public NGram(String[] words) {
        this.words = words;
    }

    public int length() {
        return words.length;
    }

    public String toString() {
        return ArrayUtils.concat(words, 0, words.length);
    }

    public boolean equals(Object object) {
        if (!(object instanceof NGram))
            return false;
        NGram o = (NGram)object;
        if (words.length != o.words.length)
            return false;
        for (int i=0; i<words.length;i++) {
            if (!words[i].equals(o.words[i]))
                return false;
        }
        return true;
    }

    public int hashCode() {
        //calculate hash code as suggested in Effective Java [Bloch]
        if (hashCalculated)
            return lazyHashCode;
        for (int i=0; i < words.length; i++) {
            lazyHashCode = 37 * lazyHashCode + words[i].hashCode();
        }
        hashCalculated = true;
        return lazyHashCode;
    }
    public String[] getWords() {
        return words;
    }
}
