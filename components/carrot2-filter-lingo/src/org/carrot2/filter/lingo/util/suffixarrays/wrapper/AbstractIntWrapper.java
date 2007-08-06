
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

package org.carrot2.filter.lingo.util.suffixarrays.wrapper;


/**
 *
 */
public abstract class AbstractIntWrapper implements IntWrapper {
    /** */

    /** DOCUMENT ME! */
    protected int[] intData;

    /**
     *
     */
    protected AbstractIntWrapper() {
        this(new int[] { -1 });
    }

    /**
     *
     */
    protected AbstractIntWrapper(int[] intData) {
        this.intData = intData;
    }

    /**
     *
     */
    public int[] asIntArray() {
        return intData;
    }

    /**
     *
     */
    public int length() {
        return intData.length - 1;
    }

    /**
     *
     */
    public void reverse() {
        int temp;

        for (int i = 0; i < ((intData.length - 1) / 2); i++) {
            temp = intData[i];
            intData[i] = intData[intData.length - i - 2];
            intData[intData.length - i - 2] = temp;
        }
    }

    /**
     *
     */
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("[ ");

        for (int i = 0; i < intData.length; i++) {
            stringBuffer.append(Integer.toString(intData[i]));
            stringBuffer.append(" ");
        }

        stringBuffer.append("]");

        return stringBuffer.toString();
    }
}
