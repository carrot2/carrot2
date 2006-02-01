package com.dawidweiss.carrot.mocktests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.mockobjects.servlet.MockServletOutputStream;

/**
 * Default implementation of {@link MockServletOutputStream}
 * returns only a stringified version of the output buffer. This unfortunately
 * prevents from acquiring binary servlet output (binary-to-character conversion
 * will corrupt bytes if platform encoding is different then Cp1250).
 * 
 * This patch writes all bytes to an internal byte array which can be then
 * acquired with {@link #getBufferContents()}.
 * 
 * @author Dawid Weiss
 */
public class PatchedMockServletOutputStream extends MockServletOutputStream {
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
    public void write(int b) throws IOException {
        super.write(b);
        this.baos.write(b);
    }

    /**
     * Returns all bytes saved to the output so far.
     */
    public byte [] getBufferContents() {
        return this.baos.toByteArray();
    }

    public void setupClearContents() {
        super.setupClearContents();
        if (baos != null) {
            this.baos.reset();
        }
    }
}
