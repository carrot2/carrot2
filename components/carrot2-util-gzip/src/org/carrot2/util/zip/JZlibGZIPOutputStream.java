package org.carrot2.util.zip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;

/**
 * An equivalent of {@link java.util.zip.GZIPOutputStream}, but
 * written on top of JZLib (and properly implementing flushes).
 * 
 * Implemented based on <a href="http://www.ietf.org/rfc/rfc1952.txt">
 * http://www.ietf.org/rfc/rfc1952.txt</a>
 *  
 * @author Dawid Weiss
 */
public class JZlibGZIPOutputStream extends OutputStream {

    private final static byte [] GZIP_HEADER = new byte [] {
        /* ID1 */ 0x1f,
        /* ID2 */ (byte) 0x8b,
        /* CM */  8 /* = deflate method */,
        /* FLG */  0,
        /* MTIME */  0, 0, 0, 0,
        /* XFL */  0,
        /* OS */  (byte) 0xff /* = UNKNOWN */,
    };

    private final OutputStream out;
    private final JZlibDeflaterOutputStream deflater;

    private volatile boolean closed = false;
    
    private final CRC32 crc32 = new CRC32();
    private long isize = 0;

    /**
     * Create a GZIP stream writing to an output stream at a default compression level.
     */
    public JZlibGZIPOutputStream(OutputStream out) throws IOException {
        this(out, JZlibDeflaterOutputStream.Z_DEFAULT_COMPRESSION, JZlibDeflaterOutputStream.DEFAULT_BUFFER_SIZE);
    }

    /**
     * Create a GZIP stream writing to an output stream at a given compression
     * level. 
     * 
     * @param out The target output stream to write to.
     * @param bufferSize Internal buffer size (for data pending to be compressed).
     * @param compressionLevel Compression level (see constants in {@link JZlibDeflaterOutputStream}.
     */
    public JZlibGZIPOutputStream(OutputStream out, int compressionLevel, int bufferSize) throws IOException {
        this.out = out;
        this.deflater = new JZlibDeflaterOutputStream(out, compressionLevel, bufferSize, true);
        emitHeader();
    }

    private void emitHeader() throws IOException {
        out.write(GZIP_HEADER);
    }

    public void close() throws IOException {
        if (closed) {
            throw new IOException("Stream already closed.");
        }

        // Close the deflater stream, but not the underlying
        // stream.
        this.deflater.softClose();

        final long crcValue = crc32.getValue();
        final long isize = this.isize;
        final byte [] trailer = new byte [] {
                (byte)  (crcValue & 0x000000ff),
                (byte) ((crcValue & 0x0000ff00) >> 8),
                (byte) ((crcValue & 0x00ff0000) >> 16),
                (byte) ((crcValue & 0xff000000) >> 24),
                (byte)  (isize & 0x000000ff),
                (byte) ((isize & 0x0000ff00) >> 8),
                (byte) ((isize & 0x00ff0000) >> 16),
                (byte) ((isize & 0xff000000) >> 24),
        };
        out.write(trailer);
        out.close();

        closed = true;
    }

    public void flush() throws IOException {
        deflater.flush();
    }

    public void write(byte[] b, int off, int len) throws IOException {
        deflater.write(b, off, len);
        isize += len;
        crc32.update(b, off, len);
    }

    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    public void write(int b) throws IOException {
        deflater.write(b);
        isize++;
        crc32.update(b);
    }

    protected void finalize() throws Throwable {
        close();
    }
}
