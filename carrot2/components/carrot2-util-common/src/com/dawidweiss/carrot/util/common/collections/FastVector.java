

/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.util.common.collections;


import java.util.*;


/**
 * <P class="classhead">
 * FastVector class (a growable array of objects with sorting capabilities).
 * </p>
 * 
 * <P class="classinfo">
 * A slight modification of java.util.Vector class that gives some more freedom in menaging of
 * vector contents. First change is that there CAN be empty gaps "inside" a vector (at some
 * index). Second change is that array of objects is now in public scope. Third there are some
 * minor methods for sorting (using java.util.Array) and binary search over sorted vector.
 * </p>
 * 
 * <P class="classinfo">
 * <b> Oryginal Sun's legal information: </b>
 * </p>
 * 
 * <p class="dischead">
 * Vector.java  1.29 95/12/01
 * </p>
 * 
 * <p class="dischead">
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 * </p>
 * 
 * <P class="disclaimer">
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * NON-COMMERCIAL purposes and without fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html" for further important
 * copyright and licensing information.
 * </p>
 * 
 * <P class="disclaimer">
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * </p>
 *
 * @author Dawid Weiss, (c) 1999 Poznan University of Technology, Institute of Computing
 *         Science.<BR> Jonathan Payne (original Vector class) (c) Sun Microsystems, Inc.<BR> Lee
 *         Boynton (original Vector class) (c) Sun Microsystems, Inc.
 */
public final class FastVector
    extends AbstractList
    implements Cloneable, List
{
    /** The buffer where elements are stored. */
    protected Object [] elementData;

    /** The number of elements in the buffer. */
    protected int elementCount;

    /**
     * The index of the last element. This field is actually vector's length. For example when you
     * have a vector like this ["a","b","c"] then the elementCount would be 3 and lastIndex would
     * be 2 (because indexes are zero-based). But if you erased two first elements, leaving [
     * null, null, "c" ] then elementCount=1 (because there is only one element in the vector!)
     * however lastIndex would still remain 2. After you erase "c" lastIndex would be set to -1
     * (so it's enough to add 1 to lastIndex to determine vector's length.
     */
    protected int lastIndex;

    /**
     * The size of the increment. If it is 0 the size of the the buffer is doubled everytime it
     * needs to grow.
     */
    protected int capacityIncrement;

    /**
     * Constructs an empty vector with the specified storage capacity and the specified
     * capacityIncrement.
     *
     * @param initialCapacity the initial storage capacity of the vector
     * @param capacityIncrement how much to increase the element's size by.
     */
    public FastVector(int initialCapacity, int capacityIncrement)
    {
        super();

        this.elementData = new Object[initialCapacity];
        this.capacityIncrement = capacityIncrement;
        this.lastIndex = -1;
    }


    /**
     * Constructs an empty vector with the specified storage capacity.
     *
     * @param initialCapacity the initial storage capacity of the vector
     */
    public FastVector(int initialCapacity)
    {
        this(initialCapacity, 0);
    }


    /**
     * Constructs an empty vector with increment count equal to 10.
     */
    public FastVector()
    {
        this(10);
    }

    /**
     * Returns the internal array of objects which this vector operates on. Any changes to that
     * array immediately affect this vector.
     */
    public Object [] getInternalArray()
    {
        return this.elementData;
    }


    /**
     * Copies the elements of this vector into the specified array.
     *
     * @param anArray the array where elements get copied into
     */
    public void copyInto(Object [] anArray)
    {
        int i = lastIndex + 1;

        while (i-- > 0)
        {
            anArray[i] = elementData[i];
        }
    }


    /**
     * Trims the vector's capacity down to size. Use this operation to minimize the storage of a
     * vector. Subsequent insertions will cause reallocation.
     */
    public void trimToSize()
    {
        int oldCapacity = elementData.length;

        if ((lastIndex + 1) < oldCapacity)
        {
            Object [] oldData = elementData;

            elementData = new Object[lastIndex + 1];

            System.arraycopy(oldData, 0, elementData, 0, lastIndex + 1);
        }
    }


    /**
     * Ensures that the vector has at least the specified capacity.
     *
     * @param minCapacity the desired minimum capacity
     */
    public void ensureCapacity(int minCapacity)
    {
        int oldCapacity = elementData.length;

        if (minCapacity > oldCapacity)
        {
            Object [] oldData = elementData;
            int newCapacity = (capacityIncrement > 0) ? (oldCapacity + capacityIncrement)
                                                      : (oldCapacity * 2);

            if (newCapacity < minCapacity)
            {
                newCapacity = minCapacity;
            }

            elementData = new Object[newCapacity];

            System.arraycopy(oldData, 0, elementData, 0, lastIndex + 1);
        }
    }


    /**
     * Returns the index of the last not-null element in the vector, starting from position
     * <code>position</code> downwards.
     *
     * @param position the position from which counting is to be begun.
     *
     * @return last element before (or on) index <code>position</code>
     */
    private int findLastIndex(int position)
    {
        int nLastItem = position;

        if (position < 0)
        {
            return (-1);
        }

        for (; nLastItem >= 0; nLastItem--)
        {
            if (elementData[nLastItem] != null)
            {
                break;
            }
        }

        return (nLastItem);
    }


    /**
     * Sets the size of the vector. If the size shrinks, the extra elements (at the end of the
     * vector) are lost; if the size increases, the new elements are set to null.
     *
     * @param newSize the new size of the vector
     */
    public void setSize(int newSize)
    {
        if (newSize > (lastIndex + 1))
        {
            ensureCapacity(newSize);
        }
        else
        {
            for (int i = newSize; i < (lastIndex + 1); i++)
            {
                if (elementData[i] != null)
                {
                    elementCount--;
                }

                elementData[i] = null;
            }
        }

        lastIndex = findLastIndex(newSize - 1);
    }


    /**
     * Returns the current capacity of the vector.
     *
     * @return Current capacity of a vector (number of elements that can be stored without
     *         reallocating internal tables).
     */
    public int capacity()
    {
        return elementData.length;
    }


    /**
     * Returns the number of elements in the vector. Note that this is not the same as the vector's
     * capacity or last index! The size of a vector is not enough to enumerate vector contents
     * using for loop! Please look at this simple example:
     * <blockquote>
     * <pre>
     * FastVector vec = new FastVector();
     *          vec.addElement( "a" ); vec.addElement( "b" ); vec.addElement( "c" );
     *          <font color=green>// vector contents = ["a","b","c"]</font>
     *          vec.eraseElement(0);
     *          <font color=green>// vector contents = [null,"b","c"]</font>
     *          <font color=green>// vec.size() = 2 while vec.lastIndex()+1 = 3!</font>
     *          for (int i=0;i&lt;vec.size();i++)
     *              System.out.println( vec.elementAt(i) );
     * </pre>
     * </blockquote>
     * 
     * <p>
     * This will cause NullPointerException of course, let's change the "for" loop:
     * <blockquote>
     * <pre>
     *          for (int i=0;i&lt;vec.size();i++)
     *              System.out.print( (vec.elementAt(i)==null? "null" : vec.elementAt(i) )+"," );
     * </pre>
     * </blockquote>
     * </p>
     * 
     * <p>
     * Now it'll print out: "null,b". As you can see it makes little sense because we have element
     * "c" that is not printed. The correct loop should look like this:
     * <blockquote>
     * <pre>
     *          for (int i=0;i&lt;vec.lastIndex()+1;i++)
     *              System.out.print( (vec.elementAt(i)==null? "null" : vec.elementAt(i) )+"," );
     * </pre>
     * </blockquote>
     * </p>
     *
     * @see #lastIndex
     */
    public int size()
    {
        return elementCount;
    }


    /**
     * Returns last element's index in the vector. Note that this is not the same as the vector's
     * size or capacity.
     *
     * @see #size
     */
    public int lastIndex()
    {
        return lastIndex;
    }


    /**
     * Returns true if the collection contains no values.
     */
    public boolean isEmpty()
    {
        return elementCount == 0;
    }


    /**
     * Returns true if the specified object is a value of the collection. This method is here
     * because of compatibility reasons (it existed in original Vector class). In FastVector you
     * can use binary search (after sorting) and it's a lot faster then scanning linearily the
     * vector's contents (what this method does).
     *
     * @param elem the desired element
     *
     * @see #bsearch
     * @see #sort
     */
    public boolean contains(Object elem)
    {
        return indexOf(elem, 0) >= 0;
    }


    /**
     * Searches for the specified object, starting from the first position and returns an index to
     * it.
     *
     * @param elem the desired element
     *
     * @return the index of the element, or -1 if it was not found.
     */
    public int indexOf(Object elem)
    {
        return indexOf(elem, 0);
    }


    /**
     * Searches for the specified object, starting at the specified position and returns an index
     * to it.
     *
     * @param elem the desired element
     * @param index the index where to start searching
     *
     * @return the index of the element, or -1 if it was not found.
     */
    public int indexOf(Object elem, int index)
    {
        for (int i = index; i < (lastIndex + 1); i++)
        {
            if (elementData[i] != null)
            {
                if (elem.equals(elementData[i]))
                {
                    return i;
                }
            }
        }

        return -1;
    }


    /**
     * Searches backwards for the specified object, starting from the last position and returns an
     * index to it.
     *
     * @param elem the desired element
     *
     * @return the index of the element, or -1 if it was not found.
     */
    public int lastIndexOf(Object elem)
    {
        return lastIndexOf(elem, lastIndex + 1);
    }


    /**
     * Searches backwards for the specified object, starting from the specified position and
     * returns an index to it.
     *
     * @param elem the desired element
     * @param index the index where to start searching
     *
     * @return the index of the element, or -1 if it was not found.
     */
    public int lastIndexOf(Object elem, int index)
    {
        for (int i = index; --i >= 0;)
        {
            if (elementData[i] != null)
            {
                if (elem.equals(elementData[i]))
                {
                    return i;
                }
            }
        }

        return -1;
    }


    /**
     * Returns the element at the specified index.
     *
     * @param index the index of the desired element
     *
     * @exception ArrayIndexOutOfBoundsException If an invalid index was given.
     */
    public Object elementAt(int index)
    {
        if (index > lastIndex)
        {
            throw new ArrayIndexOutOfBoundsException(index + " > " + lastIndex);
        }

        /*
         * Since try/catch is free, except when the exception is thrown,
         *  put in this extra try/catch to catch negative indexes and
         *  display a more informative error message.  This might not
         *  be appropriate, especially if we have a decent debugging
         *  environment - JP.
         */
        try
        {
            return elementData[index];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException(index + " < 0");
        }
    }


    /**
     * Returns the first element of the sequence.
     *
     * @exception NoSuchElementException If the sequence is empty.
     */
    public Object firstElement()
    {
        if (elementCount == 0)
        {
            throw new NoSuchElementException();
        }

        return elementData[0];
    }


    /**
     * Returns the last element of the sequence.
     *
     * @exception NoSuchElementException If the sequence is empty.
     */
    public Object lastElement()
    {
        if (elementCount == 0)
        {
            throw new NoSuchElementException();
        }

        return elementData[lastIndex];
    }


    /**
     * Sets the element at the specified index to be the specified object. The previous element at
     * that position is discarded.
     *
     * @param obj what the element is to be set to
     * @param index the specified index
     *
     * @exception ArrayIndexOutOfBoundsException If the index was invalid.
     */
    public void setElementAt(Object obj, int index)
    {
        if (index > lastIndex)
        {
            throw new ArrayIndexOutOfBoundsException(index + " > " + lastIndex);
        }

        elementData[index] = obj;
    }


    /**
     * Deletes the element at the specified index. Elements with an index greater than the current
     * index are <B>NOT</B>moved down! It's a difference when compared to
     * <code>removeElementAt(index)</code>!
     *
     * @param index the element to remove
     *
     * @exception ArrayIndexOutOfBoundsException If the index was invalid.
     *
     * @see #removeElementAt
     */
    public void eraseElementAt(int index)
    {
        if (index > lastIndex)
        {
            throw new ArrayIndexOutOfBoundsException(index + " > " + lastIndex);
        }

        if (elementData[index] != null)
        {
            elementCount--;

            elementData[index] = null;

            if (index == lastIndex)
            {
                lastIndex = findLastIndex(lastIndex - 1);
            }
        }
    }


    /**
     * Deletes the element at the specified index. Elements with an index greater than the current
     * index are moved down.
     *
     * @param index the element to remove
     *
     * @exception ArrayIndexOutOfBoundsException If the index was invalid.
     *
     * @see #eraseElementAt
     */
    public void removeElementAt(int index)
    {
        if (index > lastIndex)
        {
            throw new ArrayIndexOutOfBoundsException(index + " > " + lastIndex);
        }

        int j = lastIndex - index;

        if (elementData[index] != null)
        {
            elementData[index] = null;

            elementCount--;
        }

        if (j > 0)
        {
            System.arraycopy(elementData, index + 1, elementData, index, j);

            elementData[index + j] = null;

            lastIndex--;
        }
        else
        {
            // find new last index.
            lastIndex = findLastIndex(lastIndex - 1);
        }
    }


    /**
     * Inserts the specified object as an element at the specified index. Elements with an index
     * greater or equal to the current index are shifted up.
     *
     * @param obj the element to insert
     * @param index where to insert the new element
     *
     * @exception ArrayIndexOutOfBoundsException If the index was invalid.
     */
    public void insertElementAt(Object obj, int index)
    {
        if (index > (lastIndex + 1))
        {
            throw new ArrayIndexOutOfBoundsException(index + " > " + lastIndex + 1);
        }

        ensureCapacity(lastIndex + 2);

        if (index == (lastIndex + 1))
        {
            elementCount++;
            lastIndex++;

            elementData[index] = obj;

            return;
        }

        ;

        System.arraycopy(elementData, index, elementData, index + 1, (lastIndex + 1) - index);

        elementData[index] = obj;

        elementCount++;
        lastIndex++;
    }


    /**
     * Adds the specified object as the last element of the vector. It doesn't look up empty
     * "slots" or anything like that - the element is added after the element pointed by
     * lastIndex.
     *
     * @param obj the element to be added
     *
     * @see #size
     * @see #lastIndex
     */
    public void addElement(Object obj)
    {
        ensureCapacity(lastIndex + 2);

        elementData[lastIndex + 1] = obj;

        elementCount++;
        lastIndex++;
    }


    /**
     * Adds the specified object as the last element of the vector. It doesn't look up empty
     * "slots" or anything like that - the element is added after the element pointed by
     * lastIndex.
     *
     * @param obj the element to be added
     *
     * @return always true.
     *
     * @see #size
     * @see #lastIndex
     */
    public boolean add(Object obj)
    {
        ensureCapacity(lastIndex + 2);

        elementData[lastIndex + 1] = obj;

        elementCount++;
        lastIndex++;

        return true;
    }


    /**
     * Returns the element at specified index (null if a gap exists there).
     *
     * @throws ArrayIndexOutOfBoundsException If index is illegal for this vector.
     */
    public Object get(int index)
    {
        if (index > this.lastIndex)
        {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return elementData[index];
    }


    /**
     * Removes the element from the vector. If the object occurs more than once, only the first is
     * removed. If the object is not an element, returns false.
     *
     * @param obj the element to be removed
     *
     * @return true if the element was actually removed; false otherwise.
     */
    public boolean removeElement(Object obj)
    {
        int i = indexOf(obj);

        if (i >= 0)
        {
            removeElementAt(i);

            return true;
        }

        return false;
    }


    /**
     * Removes all null objects (empty slots) from inside a vector. This method changes relative
     * indexes and shifts objects up if it's necessary. Requires one pass through the vector's
     * contents.
     */
    public void removeGaps()
    {
        int first;
        int last;
        int moved = elementCount;
        int prevli = lastIndex;

        for (last = first = 0; moved > 0; first++)
        {
            if (elementData[first] != null)
            {
                if (first != last)
                {
                    elementData[last] = elementData[first];
                }

                moved--;
                last++;
            }
       }

         lastIndex = last - 1;

        while (last <= prevli)
        {
            elementData[last++] = null;
        }
    }


    /**
     * Clones this vector. The elements are <strong>not</strong> cloned.
     */
    public Object clone()
    {
        try
        {
            FastVector v = (FastVector) super.clone();

            v.elementData = new Object[elementCount];

            System.arraycopy(elementData, 0, v.elementData, 0, lastIndex + 1);

            return v;
        }
        catch (CloneNotSupportedException e)
        {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }


    /**
     * Converts the vector to a string. Useful for debugging.
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer();

        buf.append("[");

        for (int i = 0; i <= lastIndex; i++)
        {
            String s = (elementData[i] == null) ? "<null>"
                                                : elementData[i].toString();

            buf.append(s);

            if (i < lastIndex)
            {
                buf.append(", ");
            }
        }

        buf.append("]");

        return buf.toString();
    }


    /**
     * Sorting a vector in ascending order. This method will raise an exception if objects are not
     * comparable. All objects MUST implement Comparable interface. Sorting order is ascending
     * only in terms of values returned from <code>Comparable.compareTo</code> method. By smart
     * implementation of this method you can sort objects in any order you like.
     *
     * @see Comparable
     * @see #bsearch
     */
    public void sort()
    {
        if ((lastIndex + 1) != elementCount)
        {
            // removing gaps is faster than sorting them.
            // O(n)
            removeGaps();
        }

        Arrays.sort(elementData, 0, elementCount);
    }


    /**
     * Performs a binary search over previously sorted data. Note that if the vector is not sorted
     * the results are unpredictible. Objects MUST implement Comparable interface.
     *
     * @param key an object that is to be found in the vector.
     *
     * @return Index of key's occurence in vector or -1 if vector doesn't contain such key. Note
     *         that if there are many object matching key then the index points to one of these
     *         objects, not necessarily the first occurence.
     *
     * @see Comparable
     * @see #sort
     * @see #removeGaps
     * @see #contains
     */
    public int bsearch(Object key)
    {
        if ((lastIndex + 1) != elementCount)
        {
            // removing gaps is faster than sorting them.
            // O(n)
            removeGaps();
        }

        trimToSize();

        return (Arrays.binarySearch(elementData, key));
    }
}
