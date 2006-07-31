package org.carrot2.util.zip;

import java.io.IOException;
import java.io.OutputStream;

import com.jcraft.jzlib.*;

/**
 * An equivalent of {@link java.util.zip.DeflaterOutputStream}, but
 * written on top of JZLib (and properly implementing flushes).
 * 
 * @author Dawid Weiss
 */
public final class JZlibDeflaterOutputStream extends OutputStream {

    /** Default buffer size for data waiting to be compressed */
    final static int DEFAULT_BUFFER_SIZE = 4 * 1024;

    public final static int Z_NO_COMPRESSION = JZlib.Z_NO_COMPRESSION;
    public final static int Z_BEST_SPEED = JZlib.Z_BEST_SPEED;
    public final static int Z_BEST_COMPRESSION = JZlib.Z_BEST_COMPRESSION;
    public final static int Z_DEFAULT_COMPRESSION = JZlib.Z_DEFAULT_COMPRESSION;

    /** Deflater from JZlib */
    private final ZStream zstream;

    /** Internal buffer for uncompressed data. */
    private final byte[] buffer;
    private final int bufferLen;
    
    /** Internal buffer for compressed data. */
    private final byte[] outputBuffer = new byte [DEFAULT_BUFFER_SIZE];

    /** Position of the last byte written to the buffer + 1 */
    private int bufferPos = 0;

    /** Output stream for deflated data. */
    private final OutputStream out;
    
    /** Closed stream marker */
    private volatile boolean closed = false;

    /**
     * Creates a deflater stream with default buffer size and compression
     * level.
     * 
     * @param out The stream to write deflated data to.
     */
    public JZlibDeflaterOutputStream(final OutputStream out) {
        this(out, JZlibDeflaterOutputStream.Z_DEFAULT_COMPRESSION); 
    }
    
    /**
     * Creates a deflater stream with a given compression level and
     * default buffer size.
     * 
     * @param out The stream to write deflated data to.
     */
    public JZlibDeflaterOutputStream(final OutputStream out, final int compressionLevel) {
        this(out, compressionLevel, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Creates a deflater stream with a given buffer size and
     * the given compression level (see constants in this class).
     * 
     * @param out The stream to write deflated data to.
     * @param bufferSize Internal buffer size.
     * @param compressionLevel Compression level (see constants in this class).
     */
    public JZlibDeflaterOutputStream(OutputStream out, int compressionLevel, int bufferSize) {
        this(out, compressionLevel, bufferSize, false);
    }

    /**
     * Creates a deflater stream with a given buffer size and
     * the given compression level (see constants in this class).
     * 
     * @param out The stream to write deflated data to.
     * @param bufferSize Internal buffer size.
     * @param compressionLevel Compression level (see constants in this class).
     * @param nowrap If <code>true</code>, the deflated stream will not contain z stream headers
     * (required for GZIP compression, for example). 
     */
    public JZlibDeflaterOutputStream(OutputStream out, int compressionLevel, int bufferSize, boolean nowrap) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size must be > 0");
        }

        this.zstream = new ZStream();
        
        this.buffer = new byte [bufferSize];
        this.bufferLen = bufferSize;
        this.bufferPos = 0;
        this.out = out;
        
        zstream.deflateInit(compressionLevel, nowrap);
    }

    public void softClose() throws IOException {
        if (closed) {
            throw new IOException("Stream already closed.");
        }

        if (this.bufferPos > 0) {
            // flush remaining data
            bufferFlush(JZlib.Z_NO_FLUSH);
        }
        bufferFlush(JZlib.Z_FINISH);
        zstream.deflateEnd();
        zstream.free();

        this.out.flush();
        this.closed = true;
    }

    public void close() throws IOException {
        if (closed) {
            throw new IOException("Stream already closed.");
        }
        softClose();
        this.out.close();
        this.closed = true;
    }

    public void flush() throws IOException {
        bufferFlush(JZlib.Z_SYNC_FLUSH);
        out.flush();
    }

    public void write(final byte[] b, int off, int len) throws IOException {
        while (len > 0) {
            final int bufferSpace = bufferLen - bufferPos;
            final int chunkLen = Math.min(bufferSpace, len);
            System.arraycopy(b, off, this.buffer, bufferPos, chunkLen);

            len -= chunkLen;
            off += chunkLen;
            bufferPos += chunkLen;
            
            if (bufferPos == bufferLen) {
                bufferFlush(JZlib.Z_NO_FLUSH);
            }
        }
    }

    public final void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public final void write(int b) throws IOException {
        buffer[bufferPos] = (byte) b;
        bufferPos++;
        if (bufferPos == bufferLen) {
            bufferFlush(JZlib.Z_NO_FLUSH);
        }
    }

    protected final void finalize() throws Throwable {
        try {
            if (!closed) close();
        } catch (IOException e) {
            // Ignore exception.
        }
    }
    
    /**
     * Compresses internal buffer's data and sends deflated stuff to
     * the output stream.
     */
    private void bufferFlush(int flushMode) throws IOException {
        final ZStream z = this.zstream;
        final int outputBufferSize = outputBuffer.length;
        int err;

        // compress data pending in the input buffer.
        z.next_in = this.buffer;
        z.next_in_index = 0;
        z.avail_in = bufferPos;
        do {
            z.next_out = outputBuffer;
            z.next_out_index = 0;
            z.avail_out = outputBufferSize;
            err = z.deflate(flushMode);

            if (err != JZlib.Z_OK) {
                if (flushMode == JZlib.Z_FINISH && err == JZlib.Z_STREAM_END) {
                    // continue
                } else if (flushMode == JZlib.Z_SYNC_FLUSH && err == JZlib.Z_BUF_ERROR) {
                    // no data in the input buffer.
                    if (bufferPos != 0) {
                        throw new ZStreamException("unexpected error.");
                    }
                    return;
                } else {
                    throw new ZStreamException(z.msg);
                }
            }

            out.write(outputBuffer, 0, outputBufferSize - z.avail_out);
        } while (z.avail_in > 0 || z.avail_out == 0);

        this.bufferPos = 0;
    }
}
