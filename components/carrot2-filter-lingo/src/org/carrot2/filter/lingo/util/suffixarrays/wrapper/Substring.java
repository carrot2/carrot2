
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

package org.carrot2.filter.lingo.util.suffixarrays.wrapper;

/**
 * Represents a general substring. Contains information on the substring's
 * boundaries, absolute frequency and TF-IFD frequency.
 */
public class Substring implements Comparable {
    /** */

    /** DOCUMENT ME! */
    private int id;

    /** */

    /** DOCUMENT ME! */
    private int from;

    /** */

    /** DOCUMENT ME! */
    private int to;

    /** */

    /** DOCUMENT ME! */
    private int frequency;

    /** */

    /** DOCUMENT ME! */
    private double tfidfFrequency;

    /** */

    /** DOCUMENT ME! */
    private String stringRepresentation;

    /**
     *
     */
    public Substring(int id, int from, int to, int frequency) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.frequency = frequency;
    }

    /**
     *
     */
    public int getId() {
        return id;
    }

    /**
     *
     */
    public int getFrom() {
        return from;
    }

    /**
     *
     */
    public int getTo() {
        return to;
    }

    /**
     *
     */
    public int length() {
        return to - from;
    }

    /**
     *
     */
    public void reverse(int length) {
        int oldFrom = from;
        from = length - to;
        to = length - oldFrom;
    }

    /**
     *
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     *
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     *
     */
    public void increaseFrequency(int increment) {
        this.frequency += increment;
    }

    /**
     * For test purposes.
     */
    public boolean equals(Object o) {
        if (!(o instanceof Substring) || (((Substring) o).getId() != id)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     *
     */
    public int compareTo(Object obj) {
        if (!(obj instanceof Substring)) {
            throw new ClassCastException(obj.getClass().toString());
        }

        if (id < ((Substring) obj).getId()) {
            return -1;
        } else if (id > ((Substring) obj).getId()) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     *
     */
    public String toString() {
        return "[" + id + " " + from + " " + to + " " + frequency + "]";
    }

    /**
     * Returns the tfidfFrequency.
     *
     * @return double
     */
    public double getTfidfFrequency() {
        return tfidfFrequency;
    }

    /**
     * Sets the tfidfFrequency.
     *
     * @param tfidfFrequency The tfidfFrequency to set
     */
    public void setTfidfFrequency(double tfidfFrequency) {
        this.tfidfFrequency = tfidfFrequency;
    }

    /**
     * Returns the stringRepresentation.
     *
     * @return String
     */
    public String getStringRepresentation() {
        return stringRepresentation;
    }

    /**
     * Method setStringRepresentation.
     *
     * @param intWrapper
     */
    public void setStringRepresentation(IntWrapper intWrapper) {
        this.stringRepresentation = intWrapper.getStringRepresentation(this);
    }
}
