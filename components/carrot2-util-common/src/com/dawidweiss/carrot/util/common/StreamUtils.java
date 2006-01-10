
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

package com.dawidweiss.carrot.util.common;


import java.io.*;


/**
 * A set of common helper methods used with input streams and File objects.
 *
 * @author Dawid Weiss
 */
public final class StreamUtils
{

    /**
     * Reads an entire filesystem file into a byte array.
     */
    public static byte[] readBinaryFile(String fileName)
        throws IOException
    {
        FileInputStream is = new FileInputStream(fileName);
        return readFullyAndCloseInput(is);
    }


    /**
     * Read the contents of an opened stream until EOF (or an exception is thrown).
     * Note that this method does close the input stream, regardless whether exception
     * has been thrown or not.
     *
     * @param  input Input stream to be read.
     * @throws IOException propagated from the underlaying stream.
     */
    public static byte[] readFullyAndCloseInput(InputStream input)
        throws IOException
    {
        byte[] result = null;

        try
        {
            result = readFully(input);
        }
        finally
        {
            try
            {
                input.close();
            }
            catch (IOException e) {}
        }

        return result;
    }


    /**
     * Read the contents of an opened stream until EOF (or an exception is thrown).
     * Note that this method does NOT close the stream.
     * @param input InputStream from which data is to be read.
     * @throws IOException propagated from the underlaying stream.
     */
    public static byte[] readFully(InputStream input)
        throws IOException
    {
        final int MIN_BUFFER_SIZE = 16000;    // default buffer size is approx. 16k.
        byte[]    output          = null;
        byte[]    buffer;

        // start with the default size of the buffer.
        buffer = new byte[MIN_BUFFER_SIZE];

        while (true)
        {
            int bytesRead = input.read(buffer);

            if (bytesRead == -1)
            {
                // eof has been reached.
                if (output == null)
                {
                    output = new byte[0];
                }
                break;
            }
            else
            {
                if (bytesRead == 0)
                {
                    // an end has been reached
                    break;
                }
                else
                {
                    // append the buffer to the output array
                    if (output == null)
                    {
                        output = new byte[bytesRead];
                        System.arraycopy(buffer, 0, output, 0, bytesRead);
                    }
                    else
                    {
                        // extend the output array with the buffer.
                        byte[] newOutput = new byte[output.length + bytesRead];

                        System.arraycopy(output, 0, newOutput, 0, output.length);
                        System.arraycopy(buffer, 0, newOutput, output.length, bytesRead);

                        output = newOutput;
                    }
                }
            }
        }

        return output;
    }
    
    
    /**
     * Read the contents of an opened Reader until EOF (or an exception is thrown).
     * Note that this method does close the input stream, regardless whether exception
     * has been thrown or not.
     *
     * @param  input Reader to be read.
     * @throws IOException propagated from the underlaying stream.
     */
    public static char[] readFullyAndCloseInput(Reader input)
        throws IOException
    {
        char[] result = null;

        try
        {
            result = readFully(input);
        }
        finally
        {
            try
            {
                input.close();
            }
            catch (IOException e) {}
        }

        return result;
    }


    /**
     * Read the contents of an opened reader until EOF (or an exception is thrown).
     * Note that this method does NOT close the stream.
     * @param input Reader from which data will be read.
     * @throws IOException propagated from the underlaying stream.
     */
    public static char[] readFully(Reader input)
        throws IOException
    {
        final int MIN_BUFFER_SIZE = 16000;    // default buffer size is approx. 16k.
        char[]    output          = null;
        char[]    buffer;

        // start with the default size of the buffer.
        buffer = new char[MIN_BUFFER_SIZE];

        while (true)
        {
            int bytesRead = input.read(buffer);

            if (bytesRead == -1)
            {
                // eof has been reached.
                if (output == null)
                {
                    output = new char[0];
                }
                break;
            }
            else
            {
                if (bytesRead == 0)
                {
                    // an end has been reached
                    break;
                }
                else
                {
                    // append the buffer to the output array
                    if (output == null)
                    {
                        output = new char[bytesRead];
                        System.arraycopy(buffer, 0, output, 0, bytesRead);
                    }
                    else
                    {
                        // extend the output array with the buffer.
                        char[] newOutput = new char[output.length + bytesRead];

                        System.arraycopy(output, 0, newOutput, 0, output.length);
                        System.arraycopy(buffer, 0, newOutput, output.length, bytesRead);

                        output = newOutput;
                    }
                }
            }
        }

        return output;
    }
    

}

