package com.dawidweiss.carrot.filter.stc.suffixtree;


/**
 * The interface providing access to collection elements for use by SuffixTree.
 *
 * In order to be used by SuffixTree class (and subclasses), the object must
 * implement this interface.
 *
 * @author Dawid Weiss
 */
public interface SuffixableElement
{
    public static final Object END_OF_SUFFIX = new EOS();

    /**
     * Returns an object at specified index in this collection.
     * Object must implement Comparable interface, the return
     * value here is of type Object to allow easier passing of existing
     * classes which implement java's List interface.
     */
    public Object get(int index);


    /**
     * Returns this collection's length.
     */
    public int size();
}


/* Class for marking END-OF-STRING */
class EOS
{
    public boolean equals(Object p)
    {
        if (p == SuffixableElement.END_OF_SUFFIX) return true;

        return false;
    }


    public String toString()
    {
        return ("EOS");
    }
}



