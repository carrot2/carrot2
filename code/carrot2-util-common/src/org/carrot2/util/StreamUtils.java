package org.carrot2.util;

import java.io.*;

/**
 * A set of common helper methods used with input streams and file objects.
 */
public final class StreamUtils
{
    /** */
    private StreamUtils()
    {
        // no instances.
    }

    /**
     * Read the contents of an opened stream until EOF (or an exception is thrown). Note
     * that this method does close the input stream, regardless whether exception has been
     * thrown or not.
     * 
     * @param input Input stream to be read.
     * @throws IOException propagated from the underlying stream.
     */
    public static byte [] readFullyAndClose(InputStream input) throws IOException
    {
        try
        {
            return readFully(input);
        }
        finally
        {
            CloseableUtils.closeIgnoringException(input);
        }
    }

    /**
     * Read the contents of an opened stream until EOF (or an exception is thrown). Note
     * that this method does NOT close the stream.
     * 
     * @param input InputStream from which data is to be read.
     * @throws IOException propagated from the underlying stream.
     */
    public static byte [] readFully(final InputStream input) throws IOException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(8 * 1024);
        final byte [] buffer = new byte [8 * 1024];

        int z;
        while ((z = input.read(buffer)) > 0)
        {
            baos.write(buffer, 0, z);
        }

        return baos.toByteArray();
    }

    /**
     * Read the contents of an opened Reader until EOF (or an exception is thrown). Note
     * that this method does close the input stream, regardless whether exception has been
     * thrown or not.
     * 
     * @param input Reader to be read.
     * @throws IOException propagated from the underlying stream.
     */
    public static char [] readFullyAndClose(Reader input) throws IOException
    {
        try
        {
            return readFully(input);
        }
        finally
        {
            CloseableUtils.closeIgnoringException(input);
        }
    }

    /**
     * Read the contents of an opened reader until EOF (or an exception is thrown). Note
     * that this method does NOT close the stream.
     * 
     * @param input Reader from which data will be read.
     * @throws IOException propagated from the underlying stream.
     */
    public static char [] readFully(Reader input) throws IOException
    {
        final CharArrayWriter baos = new CharArrayWriter(8 * 1024);
        final char [] buffer = new char [8 * 1024];

        int z;
        while ((z = input.read(buffer)) > 0)
        {
            baos.write(buffer, 0, z);
        }

        return baos.toCharArray();
    }

    /**
     * Copies all available data from the input stream to the output stream. Data is
     * internally buffered.
     */
    public static void copy(InputStream in, OutputStream out, int bufSize)
        throws IOException
    {
        final byte [] buffer = new byte [bufSize];
        int tmp;
        while ((tmp = in.read(buffer)) > 0)
        {
            out.write(buffer, 0, tmp);
        }
    }
}
