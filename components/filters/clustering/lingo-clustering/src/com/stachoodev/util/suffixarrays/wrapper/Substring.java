

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.stachoodev.util.suffixarrays.wrapper;


/**
 * Represents a general substring. Contains information on the substring's  boundaries, absolute
 * frequency and TF-IFD frequency.
 */
public class Substring
    implements Comparable
{
    /** */
    private int id;

    /** */
    private int from;

    /** */
    private int to;

    /** */
    private int frequency;

    /** */
    private double tfidfFrequency;

    /** */
    private String stringRepresentation;

    /**
     *
     */
    public Substring(int id, int from, int to, int frequency)
    {
        this.id = id;
        this.from = from;
        this.to = to;
        this.frequency = frequency;
    }

    /**
     *
     */
    public int getId()
    {
        return id;
    }


    /**
     *
     */
    public int getFrom()
    {
        return from;
    }


    /**
     *
     */
    public int getTo()
    {
        return to;
    }


    /**
     *
     */
    public int length()
    {
        return to - from;
    }


    /**
     *
     */
    public void reverse(int length)
    {
        int oldFrom = from;
        from = length - to;
        to = length - oldFrom;
    }


    /**
     *
     */
    public int getFrequency()
    {
        return frequency;
    }


    /**
     *
     */
    public void setFrequency(int frequency)
    {
        this.frequency = frequency;
    }


    /**
     *
     */
    public void increaseFrequency(int increment)
    {
        this.frequency += increment;
    }


    /**
     * For test purposes.
     */
    public boolean equals(Object o)
    {
        if (!(o instanceof Substring) || (((Substring) o).getId() != id))
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    /**
     *
     */
    public int compareTo(Object obj)
    {
        if (!(obj instanceof Substring))
        {
            throw new ClassCastException(obj.getClass().toString());
        }

        if (id < ((Substring) obj).getId())
        {
            return -1;
        }
        else if (id > ((Substring) obj).getId())
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }


    /**
     *
     */
    public String toString()
    {
        return "[" + id + " " + from + " " + to + " " + frequency + "]";
    }


    /**
     * Returns the tfidfFrequency.
     *
     * @return double
     */
    public double getTfidfFrequency()
    {
        return tfidfFrequency;
    }


    /**
     * Sets the tfidfFrequency.
     *
     * @param tfidfFrequency The tfidfFrequency to set
     */
    public void setTfidfFrequency(double tfidfFrequency)
    {
        this.tfidfFrequency = tfidfFrequency;
    }


    /**
     * Returns the stringRepresentation.
     *
     * @return String
     */
    public String getStringRepresentation()
    {
        return stringRepresentation;
    }


    /**
     * Method setStringRepresentation.
     *
     * @param intWrapper
     */
    public void setStringRepresentation(IntWrapper intWrapper)
    {
        this.stringRepresentation = intWrapper.getStringRepresentation(this);
    }
}
