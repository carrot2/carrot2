
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.xsltfilter;

import java.io.*;

import javax.servlet.ServletOutputStream;

/**
 * Implementation of a {@link ServletOutputStream} that buffers all the output in memory
 * and delegates it to another stream if needed.
 * 
 * @author The original code from <code>CompressionFilter</code> example by Amy Roh and
 *         Dmitri Valdin.
 */
final class DeferredOutputStream extends ServletOutputStream
{
    /**
     * If set, this is the stream we delegate to (without buffering).
     */
    private OutputStream passthroughStream;

    /**
     * An exception saved when passing the buffered data to {@link #passthroughStream}.
     * 
     * @see #passthrough(OutputStream)
     */
    private IOException nextException;

    /**
     * If true, this stream is no longer usable (has been closed).
     */
    protected boolean closed;

    /**
     * A buffer for incoming data until we know where to delegate it.
     */
    protected ByteArrayOutputStream deferredOutput;

    /**
     * Construct a servlet output stream associated with the specified Response.
     */
    public DeferredOutputStream() throws IOException
    {
        closed = false;
        deferredOutput = new ByteArrayOutputStream();
    }

    /**
     * Returns the bytes written to the deferred stream.
     */
    public byte [] getBytes()
    {
        if (!closed)
        {
            throw new IllegalStateException(
                "Stream must be closed to acquire buffered data.");
        }

        if (passthroughStream != null)
        {
            throw new IllegalStateException(
                "All buffered data passed to the delegate stream already.");
        }

        return deferredOutput.toByteArray();
    }

    /**
     * Close this output stream
     */
    public void close() throws IOException
    {
        checkPendingExceptions();

        if (this.passthroughStream != null)
        {
            passthroughStream.close();
        }

        closed = true;
    }

    /**
     * Flush the stream.
     */
    public void flush() throws IOException
    {
        checkPendingExceptions();

        if (this.passthroughStream != null)
        {
            passthroughStream.flush();
        }
    }

    /**
     * Write the specified byte to the delegate stream or buffer it.
     */
    public void write(final int b) throws IOException
    {
        checkPendingExceptions();

        if (this.passthroughStream != null)
        {
            passthroughStream.write(b);
        }
        else
        {
            deferredOutput.write(b);
        }
    }

    /**
     * Write <code>b.length</code> bytes from the specified byte array to the delegate
     * stream or buffer it.
     * 
     * @param b The byte array to be written
     */
    public void write(final byte [] b) throws IOException
    {
        checkPendingExceptions();

        if (this.passthroughStream != null)
        {
            passthroughStream.write(b);
        }
        else
        {
            deferredOutput.write(b);
        }
    }

    /**
     * Write <code>len</code> bytes from the specified byte array, starting at the
     * specified offset, to the delegate stream or buffer it.
     * 
     * @param b The byte array containing the bytes to be written
     * @param off Zero-relative starting offset of the bytes to be written
     * @param len The number of bytes to be written
     */
    public void write(final byte [] b, final int off, final int len) throws IOException
    {
        checkPendingExceptions();

        if (this.passthroughStream != null)
        {
            passthroughStream.write(b, off, len);
        }
        else
        {
            deferredOutput.write(b, off, len);
        }
    }

    /**
     * Sets a delegate stream. Any data buffered so far is written to the delegate stream.
     * Any future requests on this stream are directly delegated to <code>stream</code>,
     * without buffering.
     */
    protected final void passthrough(OutputStream stream)
    {
        final byte [] writtenSoFar = this.deferredOutput.toByteArray();
        this.deferredOutput = null;

        try
        {
            stream.write(writtenSoFar);
        }
        catch (IOException e)
        {
            this.nextException = e;
        }

        this.passthroughStream = stream;
    }

    /**
     * Sets an {@link IOException} to be thrown on next call to any stream-access method.
     */
    protected void setExceptionOnNext(IOException e)
    {
        this.nextException = e;
    }

    /**
     * Checks if there are any pending exceptions. If so, throws it.
     * 
     * @throws IOException
     */
    private final void checkPendingExceptions() throws IOException
    {
        if (closed)
        {
            throw new IOException("Cannot write to a closed output stream");
        }

        if (nextException != null)
        {
            throw this.nextException;
        }
    }
}
