
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

package com.dawidweiss.carrot.util.http;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Wraps an element of POST request. Reads and converts the input stream on demand. <b>None of the
 * methods of this object are synchronized. When using in multi-threaded application, please make
 * sure you enforce synchronization on this object. Values returned as InputStreams should lock on
 * this object's monitor anyway, since they're instances of a nested class.</b>
 */
public class PostRequestElement
{
    /**
     * Signals that the value of this element cannot be returned because request stream has been
     * advanced.
     */
    private boolean invalid;

    /** The stream to read data from. */
    private final InputStream requestStream;

    /**
     * Encoding used for converting parameter name/ value back to a String. This is usually part of
     * the HTTP request.
     */
    private final String encoding;

    /** The name of this input parameter */
    private final String name;

    /** Indicates the value of this parameter is empty (no value chunk) */
    private boolean emptyValue;

    /**
     * Constructs a new request element object on a request stream. The constructor will attempt to
     * read the name of request parameter and if it cannot be read, it will set the endOfRequest
     * flag immediately.
     */
    public PostRequestElement(InputStream requestStream, String encoding)
        throws IOException
    {
        this.requestStream = requestStream;
        this.encoding = encoding;
        this.name = readParameterName();
    }

    /**
     * Returns true if the value of this object cannot be retrieved due to the fact that the input
     * request pointer has been advanced, or due to the fact, that it was the end of request.
     */
    public boolean isInvalid()
    {
        return invalid;
    }


    /**
     * Returns parameter name as string, or null if the parameter has been invalidated.
     */
    public String getParameterName()
    {
        if (isInvalid())
        {
            return null;
        }

        return name;
    }


    /**
     * Returns the value of this parameter as a string, decoding it from URL encoding and
     * converting to a string using the encoding specified in constructor.
     *
     * @return A string with the value of this parameter.
     *
     * @throws IOException If an IO exception occurred while reading the value.
     * @throws IllegalStateException If the parameter has been invalidated.
     */
    public String getParameterValueAsString()
        throws IOException, IllegalStateException
    {
        if (isInvalid())
        {
            throw new IllegalStateException(
                "Cannot access the value of this parameter. Request stream pointer has been advanced."
            );
        }

        // NOT SURE: empty value (end-of-stream) encodes an empty string?
        if (emptyValue)
        {
            return "";
        }

        try
        {
            byte [] value = this.getNextChunk();

            // NOT SURE: empty value (end-of-stream) encodes an empty string?
            if (value == null)
            {
                return "";
            }

            int endOfDecodedStringIndex = PostRequestElement.decodeUrlEncodedString(value);

            return new String(value, 0, endOfDecodedStringIndex, encoding);
        }
        finally
        {
            // invalidate the object after the value has been read.
            invalid = true;
        }
    }


    /**
     * Returns the value of this parameter as an InputStream. <b>The InputStream returned is sill
     * attached to the POST data input request stream. If you use any methods of this iterator,
     * such as next() or hasNext(), the InputStream returned from this method becomes invalid.</b>
     *
     * @return An InputStream with the binary value of this parameter, or null if parameter has no
     *         value.
     *
     * @throws IllegalStateException If the parameter has been invalidated.
     */
    public InputStream getParameterValueAsInputStream()
        throws IOException, IllegalStateException
    {
        if (isInvalid())
        {
            throw new IllegalStateException(
                "Cannot access the value of this parameter. Request stream pointer has been advanced."
            );
        }

        // NOT SURE: empty value (end-of-stream) encodes an empty string?
        if (emptyValue)
        {
            return null;
        }

        // we don't invalidate this object - the InputStream will do this for us.
        return new PostValueInputStream();
    }


    /**
     * Returns the value of this parameter as a string, decoding it from URL encoding and
     * converting to a string using the encoding specified in constructor.
     *
     * @return A string with the value of this parameter.
     *
     * @throws IOException If an IO exception occurred while reading the value.
     * @throws IllegalStateException If the parameter has been invalidated.
     */
    public void skipParameterValue()
        throws IOException, IllegalStateException
    {
        if (isInvalid())
        {
            throw new IllegalStateException(
                "Cannot skip the value of this parameter. Request stream pointer has been advanced."
            );
        }

        try
        {
            skipNextChunk('&');
        }
        finally
        {
            // invalidate the object after the value has been read.
            invalid = true;
        }
    }


    /* ------------------------------------------------------------------ protected section */

    /**
     * Reads parameter name from the request stream. Consumes '=' character, separating parameter
     * name from its value.
     *
     * @return A string with parameter name or null if end of request has been reached.
     */
    protected String readParameterName()
        throws IOException
    {
        byte [] encodedName = getNextChunk();

        if (encodedName == null)
        {
            // indicate end-of-request-stream condition.
            this.invalid = true;

            return null;
        }
        else
        {
            int lastNameCharIndex = decodeUrlEncodedString(encodedName);

            return new String(encodedName, 0, lastNameCharIndex);
        }
    }


    /**
     * This method decodes a string encoded with URL encoding scheme. The returned value is a byte
     * array, because nothing is known about the encoding of bytes used before encoding.
     * Applications should know request's encoding and construct an appropriate String value from
     * the byte array. The conversion is done in-place (right on the input array). The returned
     * value is an integer, which marks the end of an encoded array. This implementation is copied
     * almost verbatim from Servlet API (HttpUtils).
     *
     * @return An integer marking the end of an encoded array of bytes.
     */
    protected static int decodeUrlEncodedString(byte [] encodedString)
    {
        int outputPointer = 0;

        for (int i = 0; i < encodedString.length;)
        {
            byte c = encodedString[i];
            i++;

            switch (c)
            {
                case '+':
                    encodedString[outputPointer] = ' ';

                    break;

                case '%':

                    try
                    {
                        encodedString[outputPointer] = (byte) ((Character.digit(
                                (char) encodedString[i], 16
                            ) << 4) + Character.digit((char) encodedString[i + 1], 16));
                        i += 2;
                    }
                    catch (ArrayIndexOutOfBoundsException e)
                    {
                        // try/catch takes almost zero time. Since it's really unlikely that an url is
                        // corrupted, we do a try/catch instead of making sure there are enough bytes to
                        // convert the hex value to an integer.
                        throw new IllegalArgumentException(
                            "URLDecoder: Incomplete trailing escape (%) pattern"
                        );
                    }

                    break;

                default:
                    encodedString[outputPointer] = c;

                    break;
            }

            outputPointer++;
        }

        return outputPointer;
    }

    /**
     * An InputStream class, which takes data directly from the POST request and decodes them on
     * the fly from URL-encoding to a binary form. An object of this class is nested within the
     * PostRequestElement, so that it can monitor whether the parameter can still be read.
     */
    protected class PostValueInputStream
        extends InputStream
    {
        boolean eofReached = false;

        /**
         * Reads an URL-encoded value (one to three bytes) from the request input stream and
         * decodes it.
         *
         * @return the decoded byte.
         *
         * @throws IOException if operation fails
         * @throws IllegalStateException if the request stream pointer has been advanced.
         */
        public int read()
            throws IOException
        {
            int result;

            if (eofReached)
            {
                return -1;
            }

            if (isInvalid())
            {
                throw new IllegalStateException();
            }

            result = requestStream.read();

            // eof?
            if ((result == -1) || (result == '&'))
            {
                endOfValueReached();

                return -1;
            }

            // unencoded character?
            if (result == '+')
            {
                return ' ';
            }
            else if (result == '%')
            {
                // decode character
                result = ((Character.digit((char) requestStream.read(), 16) << 4)
                    + Character.digit((char) requestStream.read(), 16));

                // this is not a full-range check of corrupted stream values, but it
                // at least works for the case of premature eof.
                if ((result & 0xffffff00) != 0)
                {
                    throw new IOException("Malformed encoding of the input stream.");
                }

                return result;
            }
            else
            {
                return result;
            }
        }


        /**
         * Reads up to <code>len</code> bytes of data from this input stream into an array of
         * bytes.
         *
         * @param b the buffer into which the data is read.
         * @param off the start offset of the data.
         * @param len the maximum number of bytes read.
         *
         * @return the total number of bytes read into the buffer, or <code>-1</code> if there is
         *         no more data because the end of stream has been reached.
         *
         * @throws IllegalStateException if the request stream pointer has been advanced.
         * @throws IOException if an I/O error occurs.
         */
        public int read(byte [] b, int off, int len)
            throws IOException
        {
            if (b == null)
            {
                throw new NullPointerException();
            }
            else if (
                (off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length)
                    || ((off + len) < 0)
            )
            {
                throw new IndexOutOfBoundsException();
            }
            else if (len == 0)
            {
                return 0;
            }

            int c = read();

            if (c == -1)
            {
                return -1;
            }

            b[off] = (byte) c;

            int i = 1;

            for (; i < len; i++)
            {
                c = read();

                if (c == -1)
                {
                    break;
                }

                b[off + i] = (byte) c;
            }

            return i;
        }


        /**
         * Invalidates the PostRequestElement object, because it's value has been read entirely.
         */
        private void endOfValueReached()
        {
            PostRequestElement.this.invalid = true;
            eofReached = true;
        }
    }

    /* ------------------------------------------------------------------ private methods */

    /**
     * Reads the next chunk from the request.
     *
     * @return A byte array with the next sequence of bytes until '=' or '&' has been reached.
     *         These characters are not included in the return. Null is returned if end of stream
     *         has been reached.
     */
    private byte [] getNextChunk()
        throws IOException
    {
        ByteArrayOutputStream chunk = new ByteArrayOutputStream( /* default buffer size */
                20);

        int chr;

        while ((chr = requestStream.read()) != -1)
        {
            if (chr == '=')
            {
                break;
            }
            else if (chr == '&')
            {
                emptyValue = true;

                break;
            }

            chunk.write(chr);
        }

        if (chunk.size() == 0)
        {
            return null;
        }
        else
        {
            return chunk.toByteArray();
        }
    }


    /**
     * Skips the next chunk from the request.
     */
    private void skipNextChunk(char chunkSeparator)
        throws IOException
    {
        int chr;

        while ((chr = requestStream.read()) != -1)
        {
            if (chr == chunkSeparator)
            {
                break;
            }
        }
    }
}
