/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * All rights reserved.
 *
 * Refer to full text of the license "carrot2.LICENSE" in the root folder
 * of CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */

package com.dawidweiss.carrot.util.http;


import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * This class implements a non-buffered parameters iterator of a HTTP POST request. Currently only
 * <tt>application/x-www-form-urlencoded</tt> encoding is supported. <b>Multipart requests are not
 * going to be parsed correctly.</b>
 */
public class PostRequestParametersIterator
    implements Iterator
{
    /** The stream where data is read from */
    protected InputStream postStreamData;

    /** The last processed element of a POST request. */
    protected PostRequestElement lastElement;

    /** The next element to be returned. */
    protected PostRequestElement nextElement;

    /**
     * Encoding used for converting parameter name/ value back to a String. This is usually part of
     * the HTTP request.
     */
    private final String encoding;

    /**
     * Constructs a new POST data iterator.
     *
     * @param postStreamData Unparsed POST data as acquired from request.getInputStream() of
     *        doPost().
     */
    public PostRequestParametersIterator(InputStream postStreamData, String encoding)
    {
        this.postStreamData = postStreamData;
        this.encoding = encoding;
    }

    /**
     * Returns the next parameter of a POST request, or throws an exception if there are no more
     * elements to be enumerated.
     */
    public Object next()
    {
        if (nextElement == null)
        {
            if (!hasNext())
            {
                throw new NoSuchElementException("No more parameters.");
            }
        }

        lastElement = nextElement;
        nextElement = null;

        return lastElement;
    }


    /**
     * Indicates whether there are more POST parameters to enumerate. <b>This method advances the
     * request stream pointer and therefore renders the last object returned from an enumerator
     * invalid. You must process elements of the request before advancing to the next
     * parameter</b>.
     *
     * @return True if there are more POST parameters, false otherwise.
     */
    public boolean hasNext()
    {
        if (nextElement != null)
        {
            return true;
        }

        if (lastElement != null)
        {
            if (!lastElement.isInvalid())
            {
                try
                {
                    lastElement.skipParameterValue();
                }
                catch (IOException e)
                {
                    throw new NoSuchElementException(
                        "IO exception. Cannot continue: " + e.toString()
                    );
                }
            }
        }

        try
        {
            nextElement = new PostRequestElement(postStreamData, encoding);

            if (nextElement.isInvalid())
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        catch (IOException e)
        {
            // NOT SURE how we should react to erraneous stream.
            throw new RuntimeException("Cannot read data from POST stream: " + e.toString());
        }
    }


    /**
     * Required for implementation of Iterator interface. Always throws an exception.
     */
    public void remove()
    {
        throw new UnsupportedOperationException("Cannot remove parts of a POST request.");
    }
}
